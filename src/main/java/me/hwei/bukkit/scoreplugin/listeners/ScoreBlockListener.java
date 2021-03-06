package me.hwei.bukkit.scoreplugin.listeners;

import me.hwei.bukkit.scoreplugin.ScoreSignHandle;
import me.hwei.bukkit.scoreplugin.ScoreSignUtil;
import me.hwei.bukkit.scoreplugin.util.IOutput;
import me.hwei.bukkit.scoreplugin.util.OutputManager;
import me.hwei.bukkit.scoreplugin.util.PermissionManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;

public class ScoreBlockListener implements Listener {
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled())
			return;
		Block block = event.getBlock();
		if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
			return;
		}
		if(PermissionManager.GetInstance().hasPermission(event.getPlayer(), "score.break"))
			return;
		Sign sign = (Sign)block.getState();
		if(ScoreSignHandle.IsProtected(sign)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Block block = event.getBlock();
		if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
			return;
		}
		Sign sign = (Sign)block.getState();
		Player player = event.getPlayer();
		IOutput toPlayer = OutputManager.GetInstance().toSender(player);
		boolean removed = ScoreSignHandle.Remove(toPlayer, sign,
				PermissionManager.GetInstance().hasPermission(player, "score.break"));
		if(!removed) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		
		Block block = event.getBlock();
		if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
			return;
		}
		
		Sign sign = (Sign)block.getState();
		if(ScoreSignHandle.IsProtected(sign)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled())
			return;
		
		if(! PermissionManager.GetInstance().hasPermission(event.getPlayer(), "score.create"))
			return;
		ScoreSignUtil.GetInstance().create(event);
	}
}
