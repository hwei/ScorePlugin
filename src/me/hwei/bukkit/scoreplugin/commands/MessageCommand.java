package me.hwei.bukkit.scoreplugin.commands;

import org.bukkit.command.CommandSender;

import me.hwei.bukkit.util.AbstractCommand;
import me.hwei.bukkit.util.OutputManager;
import me.hwei.bukkit.util.UsageException;

public class MessageCommand extends AbstractCommand {

	public MessageCommand(String usage, String perm, AbstractCommand[] children, String message)
			throws Exception {
		super(usage, perm, children);
		this.message = message;
	}

	@Override
	protected boolean execute(CommandSender sender, MatchResult[] data)
			throws UsageException {
		OutputManager.GetInstance().toSender(sender).output(this.message);
		return true;
	}
	
	protected String message;

}
