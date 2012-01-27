package me.hwei.bukkit.scoreplugin.commands;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;
import me.hwei.bukkit.util.AbstractCommand;
import me.hwei.bukkit.util.CommandOnlyForPlayerException;
import me.hwei.bukkit.util.UsageException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaxRewardCommand extends AbstractCommand {

	public MaxRewardCommand(String usage, String perm,
			AbstractCommand[] children) throws Exception {
		super(usage, perm, children);
	}

	@Override
	protected boolean execute(CommandSender sender, MatchResult[] data)
			throws UsageException {
		Double maxReward = data[0].getDouble();
		if(maxReward == null) {
			return false;
		}
		
		if(!(sender instanceof Player)) {
			throw new CommandOnlyForPlayerException(this.coloredUsage);
		}
		
		Player player = (Player)sender;
		
		ScoreSignHandle handle = ScoreSignHandle.GetFromSight(player);
		if(handle == null) {
			return true;
		}
		
		handle.setMaxReward(maxReward);
		return true;
	}

}

