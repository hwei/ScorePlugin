package me.hwei.bukkit.scoreplugin;

import me.hwei.bukkit.scoreplugin.data.Work;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class ScoreSignUtil {
	protected ScoreSignUtil(String signHeader) {
		this.signHeader = signHeader;
	}
	public Work read(Sign sign) {
		if(sign == null)
			return null;
		String signHeader = sign.getLine(0);
		if(!signHeader.equalsIgnoreCase(this.signHeader)
				&& !signHeader.equalsIgnoreCase(ChatColor.DARK_BLUE.toString() + this.signHeader)) {
			return null;
		}
		Work work = new Work();
		work.setName(sign.getLine(1));
		String authorLine = sign.getLine(3);
		if(authorLine.startsWith(ChatColor.DARK_GRAY.toString())) {
			work.setAuthor(authorLine.substring(2));
		} else {
			work.setAuthor(authorLine);
		}
		work.setWorld(sign.getWorld().getName());
		work.setPos_x(sign.getX());
		work.setPos_y(sign.getY());
		work.setPos_z(sign.getZ());
		return work;
	}
	public void write(Sign sign, Work work) {
		if(sign == null)
			return;
		sign.setLine(0, ChatColor.DARK_BLUE.toString() + this.signHeader);
		sign.setLine(1, work.getName());
		if(work.getWork_id() == null) {
			sign.setLine(2, "");
		} else {
			sign.setLine(2,
					work.getReward() == null ?
							ChatColor.DARK_RED.toString() + "open"
							: ChatColor.YELLOW.toString() + String.format("%.2f", work.getScore()));
		}
		sign.setLine(3, ChatColor.DARK_GRAY.toString() + work.getAuthor());
		sign.update();
	}
	public void create(SignChangeEvent event) {
		if(!event.getLine(0).equalsIgnoreCase(this.signHeader)) {
			return;
		}
		event.setLine(3, ChatColor.DARK_GRAY.toString() + event.getPlayer().getName());
	}
	public double calcAuthorReward(double score, double maxReward) {
		double score_threshold = ScoreConfig.getAutherScoreThreshold();
		if(score > score_threshold) {
			return maxReward * (score - score_threshold) / (10.0 - score_threshold);
		} else if(score_threshold == 10.0) {
			return score == 10.0 ? maxReward : 0.0;
		} else {
			return 0.0;
		}
	}
	public double calcViewerReward(double score, double viewer_score) {
		double diff = Math.abs(score - viewer_score);
		double max_reword = ScoreConfig.getViewerMaxReward();
		double score_threshold = ScoreConfig.getViewerScoreThreshold();
		
		if(diff > score_threshold) {
			return 0.0;
		} else if(score_threshold <= 0.0) {
			return diff == 0.0 ? max_reword : 0.0;
		} else {
			return max_reword * (score_threshold - diff) / score_threshold;
		}
	}
	
	protected String signHeader;
	
	protected static ScoreSignUtil instance = null;
	public static ScoreSignUtil GetInstance() {
		return instance;
	}
	public static void SetUp(String signHeader) {
		instance = new ScoreSignUtil(signHeader);
	}
}
