package me.hwei.bukkit.util;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import net.milkbowl.vault.economy.Economy;

public class MoneyManager {
	protected static MoneyManager instance = null;
	public static MoneyManager GetInstance() {
		return instance;
	}
	public static void Setup(ServicesManager servicesManager) {
		RegisteredServiceProvider<Economy> economyProvider =
			servicesManager.getRegistration(Economy.class);
		Economy economy = null;
		if(economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		instance = new MoneyManager(economy);
	}
	protected Economy economy;
	
	protected MoneyManager(Economy economy) {
		this.economy = economy;
	}
	
	public String format(double amount) {
		if(economy == null)
			return Double.toString(amount);
		return economy.format(amount);
	}
	
	public boolean takeMoney(String name, double amount) {
		if(economy == null)
			return false;
		if(!economy.has(name, amount))
			return false;
		return economy.withdrawPlayer(name, amount).transactionSuccess();
	}
	
	public boolean giveMoney(String name, double amount) {
		if(economy == null)
			return false;
		return economy.depositPlayer(name, amount).transactionSuccess();
	}
   
    protected IOutput toConsole;
}
