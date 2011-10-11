package me.hwei.bukkit.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;
import com.nijikokun.register.payment.Method.MethodAccount;

public class MoneyManager {
	protected static MoneyManager instance = null;
	public static MoneyManager GetInstance() {
		return instance;
	}
	public static void Setup(PluginManager pluginManager) {
		instance = new MoneyManager(pluginManager);
	}

	protected MoneyManager(PluginManager pluginManager) {
		this.toConsole = OutputManager.GetInstance().prefix(OutputManager.GetInstance().toConsole());
		Plugin p = pluginManager.getPlugin("Register");
		if (p != null && p.isEnabled()) {
			Methods.setMethod(pluginManager);
			if (Methods.getMethod() != null) {
				this.toConsole.output(String.format("Register detected and payment methord: %s %s", Methods.getMethod().getName(), Methods.getMethod().getVersion()));
			} else {
				this.toConsole.output("Register detected but no payment methord found. No economy support available.");
			}
		} else {
			this.toConsole.output("Register not detected. No economy support available.");
		}
		
	}
	
	public String format(double amount) {
		if(Methods.getMethod() == null)
			return Double.toString(amount);
		return Methods.getMethod().format(amount);
	}
	
	public boolean takeMoney(String name, double amount) {
		Method method = Methods.getMethod();
		if(method == null)
			return false;
		if(method.hasAccount(name)) {
			MethodAccount balance = method.getAccount(name);
			if(balance.hasEnough(amount)) {
				balance.subtract(amount);
				return true;
			}
	    }
		return false;
	}
	
	public boolean giveMoney(String name, double amount) {
		Method method = Methods.getMethod();
		if(method == null)
			return false;
		if(method.hasAccount(name)) {
			MethodAccount balance = method.getAccount(name);
			balance.add(amount);
			return true;
	    }
		return false;
	}
	
   
    protected IOutput toConsole;
}
