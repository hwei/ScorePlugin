package me.hwei.bukkit.scoreplugin.util;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class PermissionManager {
	protected static PermissionManager instance = null;
	public static PermissionManager GetInstance() {
		return instance;
	}
	public static void Setup(ServicesManager servicesManager) {
		RegisteredServiceProvider<Permission> permissionProvider =
			servicesManager.getRegistration(Permission.class);
		Permission permission = null;
		if(permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		instance = new PermissionManager(permission);
	}
	protected Permission permission;
	
	protected PermissionManager(Permission permission) {
		this.permission = permission;
	}
	
	public boolean hasPermission(CommandSender sender, String permission) {
		if(this.permission == null || !(sender instanceof Player)) {
			return sender.hasPermission(permission);
		} else {
			return this.permission.has((Player)sender, permission);
		}
	}
}
