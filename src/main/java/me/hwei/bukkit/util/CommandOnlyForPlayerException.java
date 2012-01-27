package me.hwei.bukkit.util;

public class CommandOnlyForPlayerException extends UsageException {

	public CommandOnlyForPlayerException(String usage) {
		super(usage, "This command is only for players.");
	}

	private static final long serialVersionUID = 1L;

}
