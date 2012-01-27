package me.hwei.bukkit.scoreplugin.listeners;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ScoreEntityListener implements Listener {
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled())
			return;

		for (Block block : event.blockList()) {
			if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST)
				continue;
			
			Sign sign = (Sign)block.getState();
			if(ScoreSignHandle.IsProtected(sign)) {
				event.setCancelled(true);
				break;
			}
		}
	}
}
