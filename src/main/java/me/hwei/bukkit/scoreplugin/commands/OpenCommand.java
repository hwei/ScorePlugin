package me.hwei.bukkit.scoreplugin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;
import me.hwei.bukkit.scoreplugin.util.AbstractCommand;
import me.hwei.bukkit.scoreplugin.util.CommandOnlyForPlayerException;
import me.hwei.bukkit.scoreplugin.util.UsageException;

public class OpenCommand extends AbstractCommand {

	public OpenCommand(String usage, String perm, AbstractCommand[] children)
			throws Exception {
		super(usage, perm, children);
	}

	@Override
	protected boolean execute(CommandSender sender, MatchResult[] data)
			throws UsageException {
		if(!(sender instanceof Player)) {
			throw new CommandOnlyForPlayerException(this.coloredUsage);
		}
		Player player = (Player)sender;
		
		ScoreSignHandle handle = ScoreSignHandle.GetFromSight(player);
		if(handle == null) {
			return true;
		}
		
		handle.open();
		return true;
	}

}
