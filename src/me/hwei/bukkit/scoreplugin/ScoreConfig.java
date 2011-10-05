package me.hwei.bukkit.scoreplugin;

import org.bukkit.util.config.Configuration;

public class ScoreConfig {
	
	public static void reload(Configuration configuation) {
		configuation.load();
		price = getDouble(configuation, "price", 25.0);
		viewerMaxReward = getDouble(configuation, "viewer_max_reward", 500.0);
		autherMaxReward = getDouble(configuation, "auther_max_reward", 5000.0);
		viewerScoreThreshold = getDouble(configuation, "viewer_score_threshold", 1.0);
		autherScoreThreshold = getDouble(configuation, "auther_score_threshold", 6.0);
		tpPrice = getDouble(configuation, "tp_price", 50.0);
		configuation.save();
	}
	
	private static double price = 0.0;
	private static double viewerMaxReward = 0.0;
	private static double autherMaxReward = 0.0;
	private static double viewerScoreThreshold = 0.0;
	private static double autherScoreThreshold = 0.0;
	private static double tpPrice = 0.0;
	
	public static double getPrice() {
		return price;
	}
	public static double getViewerMaxReward() {
		return viewerMaxReward;
	}
	public static double getAutherMaxReward() {
		return autherMaxReward;
	}
	public static double getViewerScoreThreshold() {
		return viewerScoreThreshold;
	}
	public static double getAutherScoreThreshold() {
		return autherScoreThreshold;
	}
	public static double getTpPrice() {
		return tpPrice;
	}
	
	private static double getDouble(Configuration configuation, String path, double def) {
		double result = configuation.getDouble(path, def);
		configuation.setProperty(path, result);
		return result;
	}
}
