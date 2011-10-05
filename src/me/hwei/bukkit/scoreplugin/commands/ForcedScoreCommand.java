package me.hwei.bukkit.scoreplugin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;
import me.hwei.bukkit.util.AbstractCommand;
import me.hwei.bukkit.util.CommandOnlyForPlayerException;
import me.hwei.bukkit.util.UsageException;

public class ForcedScoreCommand extends AbstractCommand {

	public ForcedScoreCommand(String usage, String perm,
			AbstractCommand[] children) throws Exception {
		super(usage, perm, children);
	}

	@Override
	protected boolean execute(CommandSender sender, MatchResult[] data)
			throws UsageException {
		Double score = data[0].getDouble();
		if(score == null) {
			return false;
		}
		if(!(sender instanceof Player)) {
			throw new CommandOnlyForPlayerException(this.coloredUsage);
		}
		
		if(score < 0D || score > 10D) {
			throw new UsageException(this.coloredUsage, "<score> must be a number in range of 0.0~10.0.");
		}
		
		Player player = (Player)sender;
		
		ScoreSignHandle handle = ScoreSignHandle.GetFromSight(player);
		if(handle == null) {
			return true;
		}
		
		handle.setForcedScore(score);
		return true;
	}

}
