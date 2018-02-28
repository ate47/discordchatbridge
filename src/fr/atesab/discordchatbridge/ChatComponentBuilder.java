package fr.atesab.discordchatbridge;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatComponentBuilder {
	private static Class<?> getCraftBukkitClass(String name){
		return getSClass("org.bukkit.craftbukkit", name);
	}
	private static Object getMethod(String name, Object object) throws Exception {
		return getMethod(name, object, new Class<?>[] {}, new Object[] {});
	}
	private static Object getMethod(String name, Object object, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
		return object.getClass().getMethod(name, parameterTypes).invoke(object, parameters);
	}
	private static Class<?> getNMSClass(String name){
		return getSClass("net.minecraft.server", name);
	}
	private static Class<?> getSClass(String type, String name) {
		try {
			return Class.forName(type + "." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private List<ChatComponent> components;
	public ChatComponentBuilder(String text) {
		components = new ArrayList<ChatComponent>();
		components.add(new ChatComponent(text));
	}
	public ChatComponentBuilder append(String text) {
		components.add(new ChatComponent(text));
		return this;
	}
	public String build() {
		String s = "[";
		for (int i = 0; i < components.size(); i++) {
			if(i>0)s+=",";
			s+=components.get(i).getRaw();
		}
		return s+"]";
	}
	public String getFormattedText() {
		String s = "";
		ChatColor color = ChatColor.RESET;
		boolean bold = false;
		boolean italic = false;
		boolean obfuscated = false;
		boolean strikethrough = false;
		boolean underline = false;
		for (ChatComponent cc: components) {
			if(cc.getColor()!=null) color = cc.getColor();
			if(cc.getBold()!=null) bold = cc.getBold();
			if(cc.getItalic()!=null) italic = cc.getItalic();
			if(cc.getObfuscated()!=null) obfuscated = cc.getObfuscated();
			if(cc.getStrikethrough()!=null) strikethrough = cc.getStrikethrough();
			if(cc.getUnderlined()!=null) underline = cc.getUnderlined();
			s+=color.toString();
			if(bold)s+=ChatColor.BOLD;
			if(italic)s+=ChatColor.ITALIC;
			if(obfuscated)s+=ChatColor.MAGIC;
			if(strikethrough)s+=ChatColor.STRIKETHROUGH;
			if(underline)s+=ChatColor.UNDERLINE;
			s+=cc.getText();
		}
		return s;
	}
	public String getUnformattedText() {
		return getFormattedText().replaceAll(ChatColor.COLOR_CHAR+"[a-fA-F0-9]", "");
	}
	public void send(CommandSender sender) {
		if(sender instanceof Player) {
			try {
				getMethod("sendMessage", getMethod("getHandle",getCraftBukkitClass("entity.CraftPlayer").cast(sender)), 
						new Class<?>[] {getNMSClass("IChatBaseComponent")},
						new Object[] {
								getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
										.getMethod("a", String.class).invoke(null, build())
							});
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} else if (getCraftBukkitClass("command.ColouredConsoleSender").isInstance(sender)){
			sender.sendMessage(getFormattedText());
		} else {
			sender.sendMessage(getUnformattedText());
		}
	}
	public ChatComponentBuilder setBold(Boolean bold) {
		components.get(components.size()-1).setBold(bold);
		return this;
	}
	public ChatComponentBuilder setClickEvent(ClickEvent clickEvent) {
		components.get(components.size()-1).setClickEvent(clickEvent);
		return this;
	}
	public ChatComponentBuilder setColor(ChatColor color) {
		components.get(components.size()-1).setColor(color);
		return this;
	}
	public ChatComponentBuilder setHoverEvent(HoverEvent hoverEvent) {
		components.get(components.size()-1).setHoverEvent(hoverEvent);
		return this;
	}
	public ChatComponentBuilder setItalic(Boolean italic) {
		components.get(components.size()-1).setItalic(italic);
		return this;
	}
	public ChatComponentBuilder setObfuscated(Boolean obfuscated) {
		components.get(components.size()-1).setObfuscated(obfuscated);
		return this;
	}
	public ChatComponentBuilder setStrikethrough(Boolean strikethrough) {
		components.get(components.size()-1).setStrikethrough(strikethrough);
		return this;
	}
	public ChatComponentBuilder setUnderlined(Boolean underlined) {
		components.get(components.size()-1).setUnderlined(underlined);
		return this;
	}
}
