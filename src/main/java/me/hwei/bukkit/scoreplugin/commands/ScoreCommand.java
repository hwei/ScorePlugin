package me.hwei.bukkit.scoreplugin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;
import me.hwei.bukkit.scoreplugin.util.AbstractCommand;
import me.hwei.bukkit.scoreplugin.util.CommandOnlyForPlayerException;
import me.hwei.bukkit.scoreplugin.util.LanguageManager;
import me.hwei.bukkit.scoreplugin.util.UsageException;

public class ScoreCommand extends AbstractCommand {

	public ScoreCommand(String usage, String perm,
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
			LanguageManager lm = LanguageManager.GetInstance();
			throw new UsageException(this.coloredUsage, lm.getPhrase("score_range_exception"));
		}
		
		Player player = (Player)sender;
		
		ScoreSignHandle handle = ScoreSignHandle.GetFromSight(player);
		if(handle == null) {
			return true;
		}
		
		handle.giveScore(score);
		return true;
	}

}
