package me.hwei.bukkit.scoreplugin.listeners;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;
import me.hwei.bukkit.util.PermissionManager;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScorePlayerListener implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(! PermissionManager.GetInstance().hasPermission(event.getPlayer(), "score.info"))
			return;
		ScoreSignHandle handle = ScoreSignHandle.GetFromInteract(event);
		if(handle == null) {
			return;
		}
		handle.showInfo(PermissionManager.GetInstance().hasPermission(event.getPlayer(), "score.moreinfo"));
	}
}
