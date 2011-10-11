package me.hwei.bukkit.scoreplugin.commands;

import org.bukkit.command.CommandSender;

import me.hwei.bukkit.scoreplugin.ScoreConfig;
import me.hwei.bukkit.util.AbstractCommand;
import me.hwei.bukkit.util.OutputManager;
import me.hwei.bukkit.util.UsageException;

public class ReloadCommand extends AbstractCommand {

	public ReloadCommand(String usage, String perm,
			AbstractCommand[] children) throws Exception {
		super(usage, perm, children);
	}
	
	@Override
	protected boolean execute(CommandSender sender, MatchResult[] data)
			throws UsageException {
		ScoreConfig.Reload();
		OutputManager.GetInstance().toSender(sender).output("Reloaded.");
		return true;
	}

}
