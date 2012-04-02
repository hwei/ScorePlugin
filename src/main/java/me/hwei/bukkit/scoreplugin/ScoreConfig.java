package me.hwei.bukkit.scoreplugin;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;

public class ScoreConfig {
	
	public static void Setup(IConfigDataSource configDataSource) {
		ScoreConfig.configDataSource = configDataSource;
	}
	
	public interface IConfigDataSource {
		public Configuration getConfig();
		public void saveConfig();
	}
	
	public static void Reload() {
		MemoryConfiguration memConfig = new MemoryConfiguration();
		memConfig.set("price", 25D);
		memConfig.set("tp_price", 25D);
		memConfig.set("viewer_max_reward", 500D);
		memConfig.set("auther_max_reward", 5000D);
		memConfig.set("viewer_score_threshold", 1D);
		memConfig.set("auther_score_threshold", 6D);
		memConfig.set("dynmap_display_open", true);
		memConfig.set("dynmap_display_closed", true);
		Configuration config = configDataSource.getConfig();
		config.setDefaults(memConfig);
		price = getDouble(config, "price", 25D);
		tpPrice = getDouble(config, "tp_price", 25D);
		viewerMaxReward = getDouble(config, "viewer_max_reward", 500D);
		autherMaxReward = getDouble(config, "auther_max_reward", 5000D);
		viewerScoreThreshold = getDouble(config, "viewer_score_threshold", 1D);
		autherScoreThreshold =getDouble(config, "auther_score_threshold", 6D);
		dynmapDisplayOpen = getBoolean(config, "dynmap_display_open", true);
		dynmapDisplayClosed = getBoolean(config, "dynmap_display_close", true);
		
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
	private static double getDouble(Configuration config, String path , double def) {
		double value = config.getDouble(path, def);
		config.set(path, value);
		return value;
	}
	private static boolean getBoolean(Configuration config, String path , boolean def) {
		boolean value = config.getBoolean(path, def);
		config.set(path, value);
		return value;
	}
}
