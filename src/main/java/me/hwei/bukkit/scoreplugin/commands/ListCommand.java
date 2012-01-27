package me.hwei.bukkit.scoreplugin.commands;

import java.util.List;

import me.hwei.bukkit.scoreplugin.data.Storage;
import me.hwei.bukkit.scoreplugin.data.Work;
import me.hwei.bukkit.util.AbstractCommand;
import me.hwei.bukkit.util.IOutput;
import me.hwei.bukkit.util.OutputManager;
import me.hwei.bukkit.util.UsageException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends AbstractCommand {

	public ListCommand(String usage, String perm,
			AbstractCommand[] children) throws Exception {
		super(usage, perm, children);
	}

	@Override
	protected boolean execute(CommandSender sender, MatchResult[] data)
			throws UsageException {
		int pageSize = 10;
		if(data.length == 1) {
			Integer pageSizeParam = data[0].getInteger();
			if(pageSizeParam == null)
				return false;
			pageSize = pageSizeParam;
		}
		Storage storage = Storage.GetInstance();
		List<Work> recent_open_list = storage.loadOpenWorkList(pageSize);
		IOutput toSender = OutputManager.GetInstance().toSender(sender);
		if(recent_open_list.size() == 0) {
			toSender.output("No open score sign found.");
		} else {
			for(int i=0; i<recent_open_list.size(); ++i) {
				toSender.output(String.format("%d. " +
						ChatColor.GREEN + "%s" + ChatColor.WHITE +
						" author: " +
						ChatColor.DARK_GREEN + "%s",
						i + 1,
						recent_open_list.get(i).getName(),
						recent_open_list.get(i).getAuthor()
						));
			}
		}
		return true;
	}

}
