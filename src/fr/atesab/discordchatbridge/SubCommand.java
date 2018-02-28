package fr.atesab.discordchatbridge;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
	public static enum SubCommandType {
		runnable,
		suggest;
	}
	private String name;
	private String description;
	private String permission;
	private SubCommandType type;
	private String[] aliases;
	public SubCommand(String name, String permission, SubCommandType type, String description, String... aliases) {
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
		this.type = type;
		this.description = description;
	}
	public String[] getAliases() {
		return aliases;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}
	public String getPermission() {
		return permission;
	}
	public SubCommandType getType() {
		return type;
	}
	public abstract String getUsage();
	public abstract boolean onCommand(CommandSender sender, String[] args);
	public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
