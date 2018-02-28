package fr.atesab.discordchatbridge;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage.Attachment;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;

@SuppressWarnings("deprecation")
public class PluginMain extends JavaPlugin implements Listener {
	private FileConfiguration config = getConfig();
	public static final String FORMAT_PATTERN = ChatColor.COLOR_CHAR+"[0-9a-fA-FrRk-oK-O]";
	public static boolean restricted = false;
	public static boolean allowPlayerList = true;
	public static long channelid = 0;
	public static String token = "";
	public static String bot_playing = "";
	public static String panel_color = "#ff4700";
	public static String message_format_mc = "&b[Discord]&r %client%&7: &f%text%";
	public static String message_title_dc = "%player% (Minecraft chat)";
	public static String deathMessage = "%msg%";
	public static String joinMessage = "%player% joined the Minecraft server.";
	public static String quitMessage = "%player% left the Minecraft server.";
	public static List<String> groupsid = new ArrayList<String>();
	public static List<String> usersid = new ArrayList<String>();
	public static IDiscordClient discordClient;
	private static PluginMain instance;
	public void savePluginConfig() {
		config.set("channelid", channelid);
		config.set("token", token);
		config.set("panel_color", panel_color);
		config.set("bot_playing", bot_playing);
		config.set("deathMessage", deathMessage);
		config.set("joinMessage", joinMessage);
		config.set("quitMessage", quitMessage);
		config.set("restricted", restricted);
		config.set("allowPlayerList", allowPlayerList);
		config.set("groupsid", groupsid);
		config.set("usersid", usersid);
		config.set("message_format_mc", message_format_mc);
		config.set("message_title_dc", message_title_dc);
		saveConfig();
	}
	@Override
	public void onEnable() {
		instance = this;
		config.addDefault("channelid", channelid);
		config.addDefault("token", token);
		config.addDefault("panel_color", panel_color);
		config.addDefault("deathMessage", deathMessage);
		config.addDefault("joinMessage", joinMessage);
		config.addDefault("quitMessage", quitMessage);
		config.addDefault("bot_playing", bot_playing);
		config.addDefault("restricted", restricted);
		config.addDefault("allowPlayerList", allowPlayerList);
		config.addDefault("groupsid", groupsid);
		config.addDefault("usersid", usersid);
		config.addDefault("message_format_mc", message_format_mc);
		config.addDefault("message_title_dc", message_title_dc);
		config.options().copyDefaults(true);
		saveConfig();
		channelid = config.getLong("channelid");
		token = config.getString("token");
		panel_color = config.getString("panel_color");
		bot_playing = config.getString("bot_playing");
		deathMessage = config.getString("deathMessage");
		joinMessage = config.getString("joinMessage");
		quitMessage = config.getString("quitMessage");
		message_format_mc = config.getString("message_format_mc");
		message_title_dc = config.getString("message_title_dc");
		restricted = config.getBoolean("restricted");
		allowPlayerList = config.getBoolean("allowPlayerList");
		groupsid = config.getStringList("groupsid");
		usersid = config.getStringList("usersid");
		discordClient = createClient(token, true);
		discordClient.getDispatcher().registerListener(this);
		PluginCommand pc = getCommand("discordchat");
		DCBCommand cmd = new DCBCommand("discordchat");
		pc.setExecutor(cmd);
		pc.setTabCompleter(cmd);

		this.getServer().getPluginManager().registerEvents(this, this);
		super.onEnable();
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent ev) {
		if(!quitMessage.isEmpty())
			discordClient.getChannelByID(channelid).sendMessage(getEmbedObject("Quit", 
					quitMessage.replaceAll("%msg%", ev.getQuitMessage())
						.replaceAll("%player%", ev.getPlayer().getDisplayName())
						.replaceAll(FORMAT_PATTERN, "")));
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		if(!joinMessage.isEmpty())
			discordClient.getChannelByID(channelid).sendMessage(getEmbedObject("Join", 
					joinMessage.replaceAll("%msg%", ev.getJoinMessage())
						.replaceAll("%player%", ev.getPlayer().getDisplayName())
						.replaceAll(FORMAT_PATTERN, "")));
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent ev) {
		if(!deathMessage.isEmpty())
			discordClient.getChannelByID(channelid).sendMessage(getEmbedObject("Death", 
					deathMessage.replaceAll("%msg%", ev.getDeathMessage())
						.replaceAll("%player%", ev.getEntity().getDisplayName())
						.replaceAll(FORMAT_PATTERN, "")));
	}
	@EventHandler
	public void onChat(PlayerChatEvent ev) {
		if(restricted?ev.getPlayer().hasPermission("discordchat.chat.ignorerestriction"):true)
			discordClient.getChannelByID(channelid).sendMessage(getEmbedObject(message_title_dc
					.replaceAll("%player%", ev.getPlayer().getDisplayName().replaceAll(FORMAT_PATTERN, "")), 
					ev.getMessage().replaceAll(FORMAT_PATTERN, "")));
	}
	@EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
		if(!bot_playing.isEmpty())
			event.getClient().changePlayingText(bot_playing);
    }
    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
    	if(event.getChannel().getLongID() == channelid) {
    		String content = event.getMessage().getContent();
    		String message = event.getMessage().getFormattedContent();
    		if(!content.startsWith("!")) {
	    		if(restricted?(
	    				usersid.contains(String.valueOf(event.getAuthor().getLongID())) || new Object() {
	    					public boolean containGroup() {
	    						boolean a = false;
	    						if(event.getGuild()==null) return false;
	    						for (IRole r: event.getAuthor().getRolesForGuild(event.getGuild())) {
	    							if(groupsid.contains(String.valueOf(r.getLongID()))) a = true;
	    						}
	    						return a;
	    					}
	    				}.containGroup()):true) {
	    			for (Player p: Bukkit.getServer().getOnlinePlayers()) {
	    				p.sendMessage(message_format_mc.replace('&', ChatColor.COLOR_CHAR)
	    						.replaceAll("%client%", event.getGuild()!=null?event.getAuthor().getDisplayName(event.getGuild()):event.getAuthor().getName())
	    						.replaceAll("%text%", message));
	    				if(event.getMessage().getAttachments().size()!=0) {
	    					ChatComponentBuilder b = null;
	    					for (Attachment a: event.getMessage().getAttachments())
	    						(b==null?(b=new ChatComponentBuilder("Attachment"+
	    								(event.getMessage().getAttachments().size()>1?"s":"")
	    								+"> ").setColor(ChatColor.GRAY)):b.append(", ").setColor(ChatColor.GRAY))
	    						.append(a.getFilename()).setColor(ChatColor.GREEN)
	    						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
	    								new ChatComponentBuilder("Click to view attachment.").setColor(ChatColor.YELLOW).build()))
	    						.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, a.getUrl()));
	    					if(b!=null)
	    						b.send(p);
	    				}
	    			}
	    		}
    		} else {
    			if(content.equalsIgnoreCase("!playerlist") && allowPlayerList) {
    				String s = "";
	    			for (Player p: Bukkit.getServer().getOnlinePlayers()) {
	    				s+=(!s.isEmpty()?", ":"")+p.getDisplayName().replaceAll(FORMAT_PATTERN, "");
	    				
	    			}
	    			event.getChannel().sendMessage(getEmbedObject("Player list ("+Bukkit.getServer().getOnlinePlayers().size()
	    					+"/"+Bukkit.getMaxPlayers()+")", s));
    			}
    		}
    	}
    }
    public static EmbedObject getEmbedObject(String title, String description) {
    	String c = panel_color.matches("[#][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]")?panel_color:"#ffffff";
    	EmbedObject eo = new EmbedObject();
    	eo.color = Integer.valueOf(c.substring(1, 3), 16) * 256 * 256 + Integer.valueOf(c.substring(3, 5), 16) * 256 + Integer.valueOf(c.substring(5, 7), 16);
    	eo.author = null;
    	eo.title = title;
    	eo.description = description;
    	return eo;
    }
	public static IDiscordClient createClient(String token, boolean login) {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        try {
            if (login) {
                return clientBuilder.login();
            } else {
                return clientBuilder.build();
            }
        } catch (DiscordException e) {
            e.printStackTrace();
            return null;
        }
    }
	public static PluginMain getInstance() {
		return instance;
	}
	public static ChatComponentBuilder getPrefixBuilder() {
		return new ChatComponentBuilder("[").setColor(ChatColor.GRAY).append("DCB").setColor(ChatColor.AQUA).append("]").setColor(ChatColor.GRAY)
				.append(" ").setColor(ChatColor.WHITE);
	}
}
