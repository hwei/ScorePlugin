package me.hwei.bukkit.scoreplugin;

import java.util.List;

import me.hwei.bukkit.scoreplugin.data.Score;
import me.hwei.bukkit.scoreplugin.data.ScoreAggregate;
import me.hwei.bukkit.scoreplugin.data.Storage;
import me.hwei.bukkit.scoreplugin.data.Work;
import me.hwei.bukkit.scoreplugin.util.IOutput;
import me.hwei.bukkit.scoreplugin.util.LanguageManager;
import me.hwei.bukkit.scoreplugin.util.MoneyManager;
import me.hwei.bukkit.scoreplugin.util.OutputManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ScoreSignHandle {
	public static ScoreSignHandle GetFromSight(Player player) {
		Block block = player.getTargetBlock(null, 3);
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		String s_no_sign = LanguageManager.GetInstance().getPhrase("no_sign");
		if(block == null || (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)) {
			toPlayer.output(s_no_sign);
			return null;
		}
		Sign sign = (Sign)block.getState();
		Work infoFromSign = ScoreSignUtil.GetInstance().read(sign);
		if(infoFromSign == null) {
			toPlayer.output(s_no_sign);
			return null;
		}
		Work work = Storage.GetInstance().load(infoFromSign);
		return new ScoreSignHandle(player, sign, work == null ? infoFromSign : work);
	}
	
	public static ScoreSignHandle GetFromInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return null;
		Block block = event.getClickedBlock();
		if (block == null)
			return null;
			
		Sign sign = null;
		if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
			sign = (Sign)block.getState();
		} else {
			return null;
		}
		
		ScoreSignUtil signUtil = ScoreSignUtil.GetInstance();
		Work infoFromSign = signUtil.read(sign);
		if(infoFromSign == null) {
			return null;
		}
		
		Work work = Storage.GetInstance().load(infoFromSign);
		return new ScoreSignHandle(event.getPlayer(), sign, work == null ? infoFromSign : work);
	}
	
	public static boolean IsProtected(Sign sign) {
		Work infoFromSign = ScoreSignUtil.GetInstance().read(sign);
		if(infoFromSign == null) {
			return false;
		}
		Work work = Storage.GetInstance().load(infoFromSign);
		if(work == null) {
			return false;
		}
		return true;
	}
	
	public static boolean Remove(IOutput toSender, Sign sign, boolean admin) {
		Work infoFromSign = ScoreSignUtil.GetInstance().read(sign);
		if(infoFromSign == null) {
			return true;
		}
		Work work = Storage.GetInstance().load(infoFromSign);
		if(work == null) {
			return true;
		}
		if(!admin) {
			return false;
		}
		Storage.GetInstance().delete(work);
		toSender.output(LanguageManager.GetInstance().getPhrase("removed"));
		ScoreDynmap.Update();
		return true;
	}
	
	protected Player player;
	protected Sign sign;
	protected Work work;
	protected ScoreSignHandle(Player player, Sign sign, Work work) {
		this.player = player;
		this.sign = sign;
		this.work = work;
	}
	
	public void showInfo(boolean advanced) {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		LanguageManager lm = LanguageManager.GetInstance();
		if(this.work.getWork_id() == null) {
			toPlayer.output(lm.getPhrase("not_open"));
			return;
		}
		ScoreSignUtil signUtil = ScoreSignUtil.GetInstance();
		signUtil.write(sign, work);
		Storage storage = Storage.GetInstance();
		Score score = storage.load(work.getWork_id(), player.getName());
		if(this.work.getReward() == null) {
			int viewerNumber = storage.scoreCount(work.getWork_id());
			toPlayer.output(String.format(lm.getPhrase("is_open"), viewerNumber));
			
			if(advanced) {
				ScoreAggregate scoreAgg = storage.scoreAggregate(this.work.getWork_id());
				if(scoreAgg != null) {
					double authorWillWin = this.work.getScore() == null ?
							signUtil.calcAuthorReward(scoreAgg.getAverage(), this.work.getMax_reward()) :
								signUtil.calcAuthorReward(this.work.getScore(), this.work.getMax_reward());
					
					toPlayer.output(
							String.format(lm.getPhrase("advanced_info"),
								scoreAgg.getAverage(),
								scoreAgg.getMin(),
								scoreAgg.getMax(),
								scoreAgg.getSum(),
								this.work.getScore() == null ? "none" : String.format("%.2f", this.work.getScore()),
										this.work.getMax_reward(),
								authorWillWin));
				} else if(this.work.getScore() != null) {
					double authorWillWin = signUtil.calcAuthorReward(this.work.getScore(), this.work.getMax_reward());
					toPlayer.output(
							String.format(lm.getPhrase("advanced_info2"),
								this.work.getScore(),
								this.work.getMax_reward(),
								authorWillWin));
				}
			}
			
			if(score == null) {
				toPlayer.output(lm.getPhrase("how_to_score"));
				return;
			} else {
				toPlayer.output(String.format(lm.getPhrase("give_score"), score.getScore()));
				return;
			}
		} else {
			MoneyManager moneyManager = MoneyManager.GetInstance();
			toPlayer.output(String.format(lm.getPhrase("already_close"), moneyManager.format(work.getReward())));
			if(score != null) {
				toPlayer.output(String.format(lm.getPhrase("already_close_and_score"),
						score.getScore(), moneyManager.format(score.getReward())));
				return;
			}
			return;
		}
	}

	public void open() {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		LanguageManager lm = LanguageManager.GetInstance();
		if(this.work.getWork_id() != null) {
			toPlayer.output(lm.getPhrase("already_open"));
			return;
		}
		this.work.setMax_reward(ScoreConfig.getAutherMaxReward());
		Storage.GetInstance().save(this.work);
		ScoreSignUtil.GetInstance().write(this.sign, this.work);
		IOutput toAll = OutputManager.GetInstance().prefix(OutputManager.GetInstance().toAll());
		toAll.output(String.format(lm.getPhrase("new_open"),
				work.getName(), work.getAuthor()));
		ScoreDynmap.Update();
	}

	public void giveScore(double score) {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		LanguageManager lm = LanguageManager.GetInstance();
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		if(this.work.getAuthor() == player.getName()) {
			toPlayer.output(lm.getPhrase("dont_score_self"));
			return;
		}
		Storage storage = Storage.GetInstance();
		Score scoreItem = storage.load(this.work.getWork_id(), this.player.getName());
		if(scoreItem == null) {
			double price = ScoreConfig.getPrice();
			MoneyManager moneyManager = MoneyManager.GetInstance();
			if(moneyManager.takeMoney(player.getName(), price)) {
				scoreItem = new Score();
				scoreItem.setScore(score);
				scoreItem.setWork_id(this.work.getWork_id());
				scoreItem.setViewer(this.player.getName());
				storage.save(scoreItem);
				toPlayer.output(String.format(lm.getPhrase("you_give_score"),
						score,
						moneyManager.format(price)
						));
				IOutput toAll = OutputManager.GetInstance().prefix(OutputManager.GetInstance().toAll());
				toAll.output(String.format(lm.getPhrase("someone_give_score"),
						this.player.getName(),
						work.getName(),
						work.getAuthor()
						));
			} else {
				toPlayer.output(lm.getPhrase("no_money"));
			}
			return;
		} else {
			double oldScoreNumber = scoreItem.getScore();
			scoreItem.setScore(score);
			storage.save(scoreItem);
			toPlayer.output(String.format(lm.getPhrase("change_score"),
					oldScoreNumber, score));
			return;
		}
	}

	public void setForcedScore(Double forcedScore) {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		LanguageManager lm = LanguageManager.GetInstance();
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Double oldForcedScore = work.getScore();
		work.setScore(forcedScore);
		Storage storage = Storage.GetInstance();
		storage.save(work);
		toPlayer.output(String.format(lm.getPhrase("change_force_score"),
				oldForcedScore == null ? "null" : String.format("%.2f", oldForcedScore),
				forcedScore == null ? "null" : String.format("%.2f", forcedScore)
				));
	}
	
	public void clearScore() {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		LanguageManager lm = LanguageManager.GetInstance();
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Storage storage = Storage.GetInstance();
		storage.clearScore(this.work.getWork_id());
		toPlayer.output(lm.getPhrase("clear_score"));
	}

	public void close() {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		LanguageManager lm = LanguageManager.GetInstance();
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Storage storage = Storage.GetInstance();
		Double score = this.work.getScore();
		if(score == null) {
			ScoreAggregate scoreAgg = storage.scoreAggregate(this.work.getWork_id());
			if(scoreAgg == null) {
				toPlayer.output(lm.getPhrase("no_score_close"));
				return;
			}
			score = scoreAgg.getAverage();
		}
		ScoreSignUtil signutil = ScoreSignUtil.GetInstance();
		double autherReward = signutil.calcAuthorReward(score, this.work.getMax_reward());
		MoneyManager moneyManager = MoneyManager.GetInstance();
		this.work.setScore(score);
		this.work.setReward(autherReward);
		storage.save(this.work);
		signutil.write(this.sign, this.work);
		IOutput toAll = OutputManager.GetInstance().prefix(OutputManager.GetInstance().toAll());
		if(moneyManager.giveMoney(this.work.getAuthor(), autherReward)) {
			toAll.output(String.format(lm.getPhrase("announce_close"),
					this.work.getName(),
					score,
					this.work.getAuthor(),
					moneyManager.format(autherReward)
					));
		}
		List<Score> scoreList = storage.loadScoreList(this.work.getWork_id());
		Score bestViewerScore = null;
		for(Score viewerScore : scoreList) {
			double viewerReward = signutil.calcViewerReward(score, viewerScore.getScore());
			viewerScore.setReward(viewerReward);
			if(moneyManager.giveMoney(viewerScore.getViewer(), viewerReward)) {
				IOutput toThePlayer = OutputManager.GetInstance().prefix(OutputManager.GetInstance().toSender((viewerScore.getViewer())));
				toThePlayer.output(String.format(lm.getPhrase("reward_viewer"),
						work.getName(),
						score,
						viewerScore.getScore(),
						moneyManager.format(viewerReward)
						));
				if(bestViewerScore == null || viewerScore.getReward() > bestViewerScore.getReward()) {
					bestViewerScore = viewerScore;
				}
			}
		}
		if(bestViewerScore != null) {
			toAll.output(String.format(lm.getPhrase("best_viewer"),
					bestViewerScore.getViewer(),
					bestViewerScore.getScore(),
					moneyManager.format(bestViewerScore.getReward())
					));
		}
		
		storage.saveScoreList(scoreList);
		ScoreDynmap.Update();
	}
	
	public void setMaxReward(double maxReward) {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		LanguageManager lm = LanguageManager.GetInstance();
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Storage storage = Storage.GetInstance();
		double oldMaxReward = this.work.getMax_reward();
		this.work.setMax_reward(maxReward);
		storage.save(work);
		MoneyManager moneyManager = MoneyManager.GetInstance();
		toPlayer.output(String.format(lm.getPhrase("set_max_reward"),
				moneyManager.format(oldMaxReward),
				moneyManager.format(maxReward)
				));
	}
	
	protected boolean requireOpenScore(IOutput toPlayer) {
		LanguageManager lm = LanguageManager.GetInstance();
		if(this.work.getWork_id() == null) {
			toPlayer.output(lm.getPhrase("not_open"));
			return false;
		}
		if(this.work.getReward() != null) {
			toPlayer.output(String.format(lm.getPhrase("already_close"),
					MoneyManager.GetInstance().format(this.work.getReward())));
			return false;
		}
		return true;
	}
	
	
}
