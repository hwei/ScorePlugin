package me.hwei.bukkit.scoreplugin;

import java.util.List;

import me.hwei.bukkit.scoreplugin.data.Score;
import me.hwei.bukkit.scoreplugin.data.ScoreAggregate;
import me.hwei.bukkit.scoreplugin.data.Storage;
import me.hwei.bukkit.scoreplugin.data.Work;
import me.hwei.bukkit.util.IOutput;
import me.hwei.bukkit.util.MoneyManager;
import me.hwei.bukkit.util.OutputManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ScoreSignHandle {
	public static ScoreSignHandle GetFromSight(Player player) {
		Block block = player.getTargetBlock(null, 3);
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(block == null || (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)) {
			toPlayer.output("No score sign in sight.");
			return null;
		}
		Sign sign = (Sign)block.getState();
		Work infoFromSign = ScoreSignUtil.GetInstance().read(sign);
		if(infoFromSign == null) {
			toPlayer.output("No score sign in sight.");
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
		toSender.output("Removed score sign.");
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
		if(this.work.getWork_id() == null) {
			toPlayer.output("This score sign has not been opened yet.");
			return;
		}
		ScoreSignUtil signUtil = ScoreSignUtil.GetInstance();
		signUtil.write(sign, work);
		Storage storage = Storage.GetInstance();
		Score score = storage.load(work.getWork_id(), player.getName());
		if(this.work.getReward() == null) {
			int viewerNumber = storage.scoreCount(work.getWork_id());
			toPlayer.output(String.format("This score sign is open. "
					+ ChatColor.YELLOW + "%d" + ChatColor.WHITE + " players have given it a score.", viewerNumber));
			
			if(advanced) {
				ScoreAggregate scoreAgg = storage.scoreAggregate(this.work.getWork_id());
				if(scoreAgg != null) {
					double authorWillWin = this.work.getScore() == null ?
							signUtil.calcAuthorReward(scoreAgg.getAverage(), this.work.getMax_reward()) :
								signUtil.calcAuthorReward(this.work.getScore(), this.work.getMax_reward());
					
					toPlayer.output(
							String.format(
									"AVG: " + ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
								+ ", MIN " + ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
								+ ", MAX " + ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
								+ ", SUM " + ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
								+ ", FORCED SCORE: "
								+ ChatColor.YELLOW + "%s" + ChatColor.WHITE
								+ ", MAX REWARD: " + ChatColor.GOLD + "%.2f" + ChatColor.WHITE
								+ ", AUTHOR WILL WIN: " + ChatColor.GOLD + "%.2f" + ChatColor.WHITE
								+ ".",
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
							String.format(
								"FORCED SCORE: "
								+ ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
								+ ", MAX REWARD: " + ChatColor.GOLD + "%.2f" + ChatColor.WHITE
								+ ", AUTHOR WILL WIN: " + ChatColor.GOLD + "%.2f" + ChatColor.WHITE
								+ ".",
								this.work.getScore(),
								this.work.getMax_reward(),
								authorWillWin));
				}
			}
			
			if(score == null) {
				toPlayer.output("To give a score, use '/scr <score>'.");
				return;
			} else {
				toPlayer.output(String.format(
						"You have given it a score of " + ChatColor.YELLOW + "%.2f" + ChatColor.WHITE + ".", score.getScore()));
				return;
			}
		} else {
			MoneyManager moneyManager = MoneyManager.GetInstance();
			toPlayer.output(
					"This score sign has already been closed. Author has won "
					+ ChatColor.GOLD
					+ moneyManager.format(work.getReward())
					+ ChatColor.WHITE + ".");
			if(score != null) {
				toPlayer.output(
						"You have given it a score of "
						+ ChatColor.YELLOW
						+ String.format("%.2f", score.getScore())
						+ ChatColor.WHITE
						+ ", and won "+ ChatColor.GOLD
						+ moneyManager.format(score.getReward())
						+ ChatColor.WHITE + "."
						);
				return;
			}
			return;
		}
	}

	public void open() {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(this.work.getWork_id() != null) {
			toPlayer.output("Already open.");
			return;
		}
		this.work.setMax_reward(ScoreConfig.getAutherMaxReward());
		Storage.GetInstance().save(this.work);
		ScoreSignUtil.GetInstance().write(this.sign, this.work);
		IOutput toAll = OutputManager.GetInstance().prefix(OutputManager.GetInstance().toAll());
		toAll.output(String.format( "A new score sign opened! Name: "
				+ ChatColor.GREEN + "%s" + ChatColor.WHITE
				+ ", Author: " + ChatColor.DARK_GREEN + "%s" + ChatColor.WHITE + ".",
				work.getName(), work.getAuthor()));
	}

	public void giveScore(double score) {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		if(this.work.getAuthor() == player.getName()) {
			toPlayer.output("Sorry, you could not give score to yourself.");
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
				toPlayer.output(String.format(
						"You have given a score of "
						+ ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
						+ " , and paid "
						+ ChatColor.GOLD + "%s" + ChatColor.WHITE
						+ ".",
						score,
						moneyManager.format(price)
						));
				IOutput toAll = OutputManager.GetInstance().prefix(OutputManager.GetInstance().toAll());
				toAll.output(String.format(
						"" + ChatColor.DARK_GREEN + "%s" + ChatColor.WHITE
						+ " has given a score to "
						+ ChatColor.GREEN + "%s" + ChatColor.WHITE
						+ " ( author: " + ChatColor.DARK_GREEN + "%s" + ChatColor.WHITE
						+ " ).",
						this.player.getName(),
						work.getName(),
						 work.getAuthor()
						));
			} else {
				toPlayer.output("You do not have enough money to give score.");
			}
			return;
		} else {
			double oldScoreNumber = scoreItem.getScore();
			scoreItem.setScore(score);
			storage.save(scoreItem);
			toPlayer.output(String.format(
					"Changed score from "
					+ ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
					+ " to " + ChatColor.YELLOW + "%.2f" + ChatColor.WHITE + ".",
					oldScoreNumber, score));
			return;
		}
	}

	public void setForcedScore(Double forcedScore) {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Double oldForcedScore = work.getScore();
		work.setScore(forcedScore);
		Storage storage = Storage.GetInstance();
		storage.save(work);
		toPlayer.output(String.format(
				"Change forced score form " +
				ChatColor.YELLOW + "%s" + ChatColor.WHITE +
				" to " +
				ChatColor.YELLOW + "%s" + ChatColor.WHITE +
				" .",
				oldForcedScore == null ? "null" : String.format("%.2f", oldForcedScore),
				forcedScore == null ? "null" : String.format("%.2f", forcedScore)
				));
	}
	
	public void clearScore() {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Storage storage = Storage.GetInstance();
		storage.clearScore(this.work.getWork_id());
		toPlayer.output("Cleared all scores from viewers.");
	}

	public void close() {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Storage storage = Storage.GetInstance();
		Double score = this.work.getScore();
		if(score == null) {
			ScoreAggregate scoreAgg = storage.scoreAggregate(this.work.getWork_id());
			if(scoreAgg == null) {
				toPlayer.output("No player has given it score. Can not close it. To make it closable, give it a forced score.");
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
			toAll.output(String.format(
					"The score of "
					+ ChatColor.GREEN + "%s" + ChatColor.WHITE
					+ " is "
					+ ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
					+ ". The author " + ChatColor.DARK_GREEN + "%s" + ChatColor.WHITE + " has won "
					+ ChatColor.GOLD + "%s" + ChatColor.WHITE
					+ ".",
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
				toThePlayer.output(String.format("The score of work "
						+ ChatColor.GREEN + "%s" + ChatColor.WHITE
						+ " is "
						+ ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
						+ ". "
						+ "You have given a score of "
						+ ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
						+ " and won "
						+ ChatColor.GOLD + "%s" + ChatColor.WHITE
						+ ".",
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
			toAll.output(String.format(
					"The best viewer is " + ChatColor.DARK_GREEN + "%s" + ChatColor.WHITE
					+ ". He / she has given a score of " + ChatColor.YELLOW + "%.2f" + ChatColor.WHITE
					+ " and won " + ChatColor.GOLD + "%s" + ChatColor.WHITE + ".",
					bestViewerScore.getViewer(),
					bestViewerScore.getScore(),
					moneyManager.format(bestViewerScore.getReward())
					));
		}
		
		storage.saveScoreList(scoreList);
	}
	
	public void setMaxReward(double maxReward) {
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(!this.requireOpenScore(toPlayer)) {
			return;
		}
		Storage storage = Storage.GetInstance();
		double oldMaxReward = this.work.getMax_reward();
		this.work.setMax_reward(maxReward);
		storage.save(work);
		MoneyManager moneyManager = MoneyManager.GetInstance();
		toPlayer.output(String.format(
				"Set max reward from " +
				ChatColor.GOLD + "%s" + ChatColor.WHITE +
				" to " +
				ChatColor.GOLD + "%s" + ChatColor.WHITE +
				" .",
				moneyManager.format(oldMaxReward),
				moneyManager.format(maxReward)
				));
	}
	
	protected boolean requireOpenScore(IOutput toPlayer) {
		if(this.work.getWork_id() == null) {
			toPlayer.output("This score sign has not been opened yet.");
			return false;
		}
		if(this.work.getReward() != null) {
			toPlayer.output(String.format(
					"This score sign has already been closed. Author has won "
					+ ChatColor.GOLD
					+ "%s"
					+ ChatColor.WHITE + ".",
					MoneyManager.GetInstance().format(this.work.getReward())));
			return false;
		}
		return true;
	}
	
	
}
