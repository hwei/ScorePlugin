package me.hwei.bukkit.scoreplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.PersistenceException;

import me.hwei.bukkit.scoreplugin.ScoreConfig.IConfigDataSource;
import me.hwei.bukkit.scoreplugin.commands.*;
import me.hwei.bukkit.scoreplugin.data.Score;
import me.hwei.bukkit.scoreplugin.data.ScoreAggregate;
import me.hwei.bukkit.scoreplugin.data.Storage;
import me.hwei.bukkit.scoreplugin.data.Work;
import me.hwei.bukkit.scoreplugin.listeners.ScoreBlockListener;
import me.hwei.bukkit.scoreplugin.listeners.ScoreEntityListener;
import me.hwei.bukkit.scoreplugin.listeners.ScorePlayerListener;
import me.hwei.bukkit.util.AbstractCommand;
import me.hwei.bukkit.util.IOutput;
import me.hwei.bukkit.util.MoneyManager;
import me.hwei.bukkit.util.OutputManager;
import me.hwei.bukkit.util.PermissionManager;
import me.hwei.bukkit.util.PermissionsException;
import me.hwei.bukkit.util.UsageException;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;


public class ScorePlugin extends JavaPlugin
{	
	protected IOutput toConsole = null;
	protected boolean enable = false;
	protected Map<String, AbstractCommand> topCommands = null;
	
	@Override
	public void onDisable() {
		if(this.enable) {
			this.enable = false;
			this.toConsole.output("Disabled.");
		}
		this.toConsole = null;
		this.topCommands = null;
	}

	@Override
	public void onEnable() {
		IOutput toConsole = new IOutput() {
			@Override
			public void output(String message) {
				getServer().getConsoleSender().sendMessage(message);
			}
		};
		IOutput toAll = new IOutput() {
			@Override
			public void output(String message) {
				getServer().broadcastMessage(message);
			}
		};
		OutputManager.IPlayerGetter playerGetter = new OutputManager.IPlayerGetter() {
			@Override
			public Player get(String name) {
				return getServer().getPlayer(name);
			}
		};
		
		OutputManager.Setup(
				"[" + ChatColor.YELLOW + this.getDescription().getName() + ChatColor.WHITE + "] ",
				toConsole, toAll, playerGetter);
		this.toConsole = OutputManager.GetInstance().prefix(toConsole);
		
		ScoreSignUtil.Setup("[" + this.getDescription().getName() + "]");
		
		ScoreConfig.Setup(new IConfigDataSource(){
			@Override
			public Configuration getConfig() {
				return ScorePlugin.this.getConfig();
			}
			@Override
			public void saveConfig() {
				ScorePlugin.this.saveConfig();
			}
		});
		ScoreConfig.Reload();
		this.saveConfig();
		
		this.setupDatabase();
		Storage.SetUp(this.getDatabase());
		
		if(!this.setupCommands()) {
			this.toConsole.output(this.getDescription().getVersion() + ChatColor.RED + " is just broken. :'(");
			return;
		}
		
		ScoreBlockListener blockListener = new ScoreBlockListener();
		PluginManager pluginManager = this.getServer().getPluginManager();
		pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, new ScorePlayerListener(), Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.ENTITY_EXPLODE, new ScoreEntityListener(), Priority.Normal, this);
		
		MoneyManager.Setup(getServer().getServicesManager());
		PermissionManager.Setup(getServer().getServicesManager());
		
		this.enable = true;
		this.toConsole.output(this.getDescription().getVersion() + " Enabled. Developed by " + this.getDescription().getAuthors().get(0) + ".");
	}
	
	protected void setupDatabase() {
		try {
			this.getDatabase().find(Work.class).findRowCount();
			this.getDatabase().find(Score.class).findRowCount();
		} catch (PersistenceException ex) {
			this.toConsole.output("Installing database for " + this.getDescription().getName() + " due to first time usage");
			this.installDDL();
        }
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		AbstractCommand topCommand = this.topCommands.get(command.getName());
		if(topCommand == null) {
			return false;
		}
		try {
			if(!topCommand.execute(sender, args)) {
				topCommand.showUsage(sender, command.getName());
			}
		} catch (PermissionsException e) {
			sender.sendMessage(String.format(ChatColor.RED.toString() + "You do not have permission of %s", e.getPerms()));
		} catch (UsageException e) {
			sender.sendMessage("Usage: " + ChatColor.YELLOW + command.getName() + " " + e.getUsage());
			sender.sendMessage(String.format(ChatColor.RED.toString() + e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Work.class);
        list.add(Score.class);
        list.add(ScoreAggregate.class);
        return list;
	}
	
	protected boolean setupCommands() {
		try {
			AbstractCommand[] childCommands = new AbstractCommand[] {
					new OpenCommand(
							"open  Open a score sign.",
							"score.open",
							null),
					new ScoreCommand(
							"<score>  Give a score. (Range: 0.0~10.0)",
							"score.score",
							null),
					new ForcedScoreCommand(
							"set <score>  Set a forced score. (Range: 0.0~10.0)",
							"score.forcedscore",
							null),
					new UnsetForcedScoreCommand(
							"unset  Unset a forced score.",
							"score.forcedscore",
							null),
					new ClearCommand(
							"clear  Clear all scores given by viewers.",
							"score.clear",
							null),
					new CloseCommand(
							"close  Close a score sign and distrubute rewards",
							"score.close",
							null),
					new MaxRewardCommand(
							"maxreward <reward>  Set max reward of a score sign.",
							"score.maxreward",
							null),
					new ListCommand(
							"list [pagesize]  List recent open score signs.",
							"score.list",
							null),
					new TeleportCommand(
							"tp <num>  Teleport to a score sign in list.",
							"score.tp",
							null,
							new TeleportCommand.IWorldGetter() {
								@Override
								public World get(String name) {
									return getServer().getWorld(name);
								}
							}),
					new ReloadCommand(
							"reload  Reload configuration.",
							"score.reload",
							null)
			};
			
			String pluginInfo = String.format(
					ChatColor.YELLOW.toString() + "%s " + ChatColor.GOLD + "%s" + 
					ChatColor.WHITE + ". Type /scr ? to get help.",
					this.getDescription().getName(),
					this.getDescription().getVersion());
			MessageCommand scoreCommand = new MessageCommand(
					"  Get plugin info.",
					"score",
					childCommands, pluginInfo);
			this.topCommands = new TreeMap<String, AbstractCommand>();
			this.topCommands.put("score", scoreCommand);
		} catch (Exception e) {
			this.toConsole.output("Can not setup commands!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
