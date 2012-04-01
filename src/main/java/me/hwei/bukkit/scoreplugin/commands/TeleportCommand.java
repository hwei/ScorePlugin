package me.hwei.bukkit.scoreplugin.commands;

import me.hwei.bukkit.scoreplugin.ScoreConfig;
import me.hwei.bukkit.scoreplugin.data.Storage;
import me.hwei.bukkit.scoreplugin.data.Work;
import me.hwei.bukkit.scoreplugin.util.AbstractCommand;
import me.hwei.bukkit.scoreplugin.util.CommandOnlyForPlayerException;
import me.hwei.bukkit.scoreplugin.util.IOutput;
import me.hwei.bukkit.scoreplugin.util.LanguageManager;
import me.hwei.bukkit.scoreplugin.util.MoneyManager;
import me.hwei.bukkit.scoreplugin.util.OutputManager;
import me.hwei.bukkit.scoreplugin.util.UsageException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends AbstractCommand {

	public TeleportCommand(String usage, String perm,
			AbstractCommand[] children, IWorldGetter worldGetter) throws Exception {
		super(usage, perm, children);
		this.worldGetter = worldGetter;
	}
	
	public interface IWorldGetter {
		public World get(String name);
	}
	
	protected IWorldGetter worldGetter;

	@Override
	protected boolean execute(CommandSender sender, MatchResult[] data)
			throws UsageException {
		Integer tpNum = data[0].getInteger();
		if(tpNum == null)
			return false;
		
		LanguageManager lm = LanguageManager.GetInstance();
		if(tpNum <=0 ) {
			throw new UsageException(this.coloredUsage, lm.getPhrase("tp_num_exception"));
		}
		
		if(!(sender instanceof Player)) {
			throw new CommandOnlyForPlayerException(this.coloredUsage);
		}
		Player player = (Player)sender;
		
		Storage storage = Storage.GetInstance();
		Work work = storage.loadOpenWorkAt(tpNum - 1);
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		if(work == null) {
			toPlayer.output(lm.getPhrase("no_tp_target"));
			return true;
		}
		double tp_price = ScoreConfig.getTpPrice();
		MoneyManager moneyManager = MoneyManager.GetInstance();
		if(tp_price == 0D || moneyManager.takeMoney(player.getName(), tp_price)) {
			toPlayer.output(String.format(lm.getPhrase("tp_start"),
					moneyManager.format(tp_price),
					work.getName()
			));
			Location l = new Location(
					this.worldGetter.get(work.getWorld()),
					work.getPos_x() + 0.5D,
					work.getPos_y() + 0.5D,
					work.getPos_z() + 0.5D);
			player.teleport(l);
		} else {
			toPlayer.output(String.format(lm.getPhrase("tp_no_money"),
					moneyManager.format(tp_price)
					));
		}
		return true;
	}

}
