package me.hwei.bukkit.scoreplugin.listeners;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class ScorePlayerListener extends PlayerListener {
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.getPlayer().hasPermission("score.info"))
			return;
		ScoreSignHandle handle = ScoreSignHandle.GetFromInteract(event);
		if(handle == null) {
			return;
		}
		handle.showInfo(event.getPlayer().hasPermission("score.moreinfo"));
	}
}
