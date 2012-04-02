package me.hwei.bukkit.scoreplugin;

import org.bukkit.configuration.Configuration;

public class ScoreConfig {
	
	public static void Setup(IConfigDataSource configDataSource) {
		ScoreConfig.configDataSource = configDataSource;
	}
	
	public interface IConfigDataSource {
		public Configuration getConfig();
		public void saveConfig();
	}
	
	public static void Reload() {
		Configuration config = configDataSource.getConfig();
		price = config.getDouble("price");
		tpPrice = config.getDouble("tp_price");
		viewerMaxReward = config.getDouble("viewer_max_reward");
		autherMaxReward = config.getDouble("auther_max_reward");
		viewerScoreThreshold = config.getDouble("viewer_score_threshold");
		autherScoreThreshold = config.getDouble("auther_score_threshold");
		dynmapDisplayOpen = config.getBoolean("dynmap_display_open");
		dynmapDisplayClosed = config.getBoolean("dynmap_display_close");
		configDataSource.saveConfig();
	}
	
	private static IConfigDataSource configDataSource = null;
	private static double price = 0.0;
	private static double viewerMaxReward = 0.0;
	private static double autherMaxReward = 0.0;
	private static double viewerScoreThreshold = 0.0;
	private static double autherScoreThreshold = 0.0;
	private static boolean dynmapDisplayOpen = true;
	private static boolean dynmapDisplayClosed = true;
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
	public static boolean getDynmapDisplayOpen() {
		return dynmapDisplayOpen;
	}
	public static boolean getDynmapDisplayClosed() {
		return dynmapDisplayClosed;
	}
}
