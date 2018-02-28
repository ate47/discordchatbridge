package fr.atesab.discordchatbridge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fr.atesab.discordchatbridge.HoverEvent.Action;
import fr.atesab.discordchatbridge.SubCommand.SubCommandType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class DCBCommand implements TabCompleter, CommandExecutor {
	private final String name;
	public DCBCommand(String name) {
		this.name = name;
		subCommands = new ArrayList<SubCommand>();
		subCommands.add(new SubCommand("save","discordchat.admin.save",SubCommandType.runnable,"Save config") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				return new ArrayList<String>();
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				PluginMain.getPrefixBuilder().append("Saving config...").setColor(ChatColor.YELLOW);
				PluginMain.getInstance().savePluginConfig();
				return true;
			}
			public String getUsage() {return getName();}
		});
		subCommands.add(new SubCommand("restart","discordchat.admin.restart",SubCommandType.runnable,"Restart bot") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				return new ArrayList<String>();
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				PluginMain.getPrefixBuilder().append("Restarting bot...").setColor(ChatColor.YELLOW);
				PluginMain.discordClient.logout();
				PluginMain.discordClient = PluginMain.createClient(PluginMain.token, true);
				PluginMain.discordClient.getDispatcher().registerListener(PluginMain.getInstance());
				return true;
			}
			public String getUsage() {return getName();}
		});
		subCommands.add(new SubCommand("setchannelid","discordchat.admin.setchannelid",SubCommandType.suggest,"Set channel bridge id") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				return new ArrayList<String>();
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				if(args.length==1) {
					long l;
					try {
						l = Long.valueOf(args[0]);
					} catch (Exception e) {
						return false;
					}
					PluginMain.channelid = l;
					PluginMain.getInstance().savePluginConfig();
					PluginMain.getPrefixBuilder().append("Channel Id set and saved.").setColor(ChatColor.YELLOW).send(sender);
				} else return false;
				return true;
			}
			public String getUsage() {return getName()+" (id)";}
		});
		subCommands.add(new SubCommand("clients","discordchat.admin.modclients",SubCommandType.runnable,"Change restricted clients") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				List<String> l = new ArrayList<String>();
				if(args.length<2) {
					l.add("add");
					l.add("del");
				} else if(args.length<3 && args[0].equalsIgnoreCase("del"))
					l.addAll(PluginMain.usersid);
				return getTabCompletion(l, args);
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				if(args.length==0) {
					ChatComponentBuilder b = PluginMain.getPrefixBuilder().append("Clients : ").setColor(ChatColor.YELLOW);
					for (String id: PluginMain.usersid) {
						b.append("\n[-]").setColor(ChatColor.RED)
						.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentBuilder("Remove this client").setColor(ChatColor.YELLOW).build()))
						.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+getMainCommandName()+" "+getName()+" del "+id))
						.append(" - ").setColor(ChatColor.GRAY).append(id).setColor(ChatColor.WHITE);
					}
					b.append("\n[+] Add a client").setColor(ChatColor.GREEN)
					.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentBuilder("Add a client").setColor(ChatColor.YELLOW).build()))
					.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+getMainCommandName()+" "+getName()+" add "));
					b.send(sender);
				} else if (args[0].equalsIgnoreCase("add") && args.length==2) {
					if(!PluginMain.usersid.contains(args[1])) {
						PluginMain.usersid.add(args[1]);
						PluginMain.getInstance().savePluginConfig();
						PluginMain.getPrefixBuilder().append("UserId added.").setColor(ChatColor.YELLOW).send(sender);
					} else PluginMain.getPrefixBuilder().append("the UserId already exist.").setColor(ChatColor.RED).send(sender);
				} else if (args[0].equalsIgnoreCase("del") && args.length==2) {
					if(PluginMain.usersid.contains(args[1])) {
						PluginMain.usersid.remove(args[1]);
						PluginMain.getInstance().savePluginConfig();
						PluginMain.getPrefixBuilder().append("UserId removed.").setColor(ChatColor.YELLOW).send(sender);
					} else PluginMain.getPrefixBuilder().append("the UserId not exist.").setColor(ChatColor.RED).send(sender);
				} else return false;
				return true;
			}
			public String getUsage() {return getName()+" [add|del] (id)";}
		});
		subCommands.add(new SubCommand("groups","discordchat.admin.modgroups",SubCommandType.runnable,"Change restricted groups") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				List<String> l = new ArrayList<String>();
				if(args.length<2) {
					l.add("add");
					l.add("del");
				} else if(args.length<3 && args[0].equalsIgnoreCase("del"))
					l.addAll(PluginMain.groupsid);
				return getTabCompletion(l, args);
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				if(args.length==0) {
					ChatComponentBuilder b = PluginMain.getPrefixBuilder().append("Groups : ").setColor(ChatColor.YELLOW);
					for (String id: PluginMain.groupsid) {
						b.append("\n[-]").setColor(ChatColor.RED)
						.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentBuilder("Remove this group").setColor(ChatColor.YELLOW).build()))
						.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+getMainCommandName()+" "+getName()+" del "+id))
						.append(" - ").setColor(ChatColor.GRAY).append(id).setColor(ChatColor.WHITE);
					}
					b.append("\n[+] Add a client").setColor(ChatColor.GREEN)
					.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentBuilder("Add a group").setColor(ChatColor.YELLOW).build()))
					.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+getMainCommandName()+" "+getName()+" add "));
					b.send(sender);
				} else if (args[0].equalsIgnoreCase("add") && args.length==2) {
					if(!PluginMain.groupsid.contains(args[1])) {
						PluginMain.groupsid.add(args[1]);
						PluginMain.getPrefixBuilder().append("GroupId added.").setColor(ChatColor.YELLOW).send(sender);
					} else new ChatComponentBuilder("The GroupId Id already exist.").setColor(ChatColor.RED).send(sender);
				} else if (args[0].equalsIgnoreCase("del") && args.length==2) {
					if(PluginMain.groupsid.contains(args[1])) {
						PluginMain.groupsid.remove(args[1]);
						PluginMain.getPrefixBuilder().append("UserId removed.").setColor(ChatColor.YELLOW).send(sender);
					} else PluginMain.getPrefixBuilder().append("The GroupId not exist.").setColor(ChatColor.RED).send(sender);
				} else return false;
				return true;
			}
			public String getUsage() {return getName()+" [add|del] (id...)";}
		});

		subCommands.add(new SubCommand("channellist","discordchat.admin.channellist",SubCommandType.runnable,"Show Discord channel list") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				return new ArrayList<String>();
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				ChatComponentBuilder b = PluginMain.getPrefixBuilder().append("Channel List : ");
				for (IGuild g: PluginMain.discordClient.getGuilds()) {
					b.append("\n- ").setColor(ChatColor.GRAY).append(g.getName()).setColor(ChatColor.WHITE)
						.append(" : ").setColor(ChatColor.GRAY);
					boolean f = false;
					for (IChannel c: g.getChannels()) {
						if(f) b.append(", ").setColor(ChatColor.GRAY);
						else f = true;
						b.append(c.getLongID()+" ("+c.getName()+")");
						if(c.getLongID()==PluginMain.channelid)
							b.setColor(ChatColor.LIGHT_PURPLE);
						else b.setColor(ChatColor.WHITE)
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ChatComponentBuilder("Click to define bridge channel id").setColor(ChatColor.YELLOW).build()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+getMainCommandName()+" setchannelid "+c.getLongID()));
							
					}
					b.append(".").setColor(ChatColor.GRAY);
				}
				b.send(sender);
				return true;
			}
			public String getUsage() {return getName();}
		});
		subCommands.add(new SubCommand("clientslist","discordchat.admin.clientslist",SubCommandType.runnable,"Show Discord client list") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				return new ArrayList<String>();
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				ChatComponentBuilder b = PluginMain.getPrefixBuilder().append("Client List : ");
				for (IGuild g: PluginMain.discordClient.getGuilds()) {
					b.append("\n- ").setColor(ChatColor.GRAY).append(g.getName()).setColor(ChatColor.WHITE)
						.append(" : ").setColor(ChatColor.GRAY);
					boolean f = false;
					for (IUser u: g.getUsers()) {
						if(f) b.append(", ").setColor(ChatColor.GRAY);
						else f = true;
						b.append(u.getLongID()+" ("+u.getName()+")");
						if(PluginMain.usersid.contains(String.valueOf(u.getLongID())))
							b.setColor(ChatColor.LIGHT_PURPLE)
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ChatComponentBuilder("Click to remove user id").setColor(ChatColor.YELLOW).build()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+getMainCommandName()+" clients del "+u.getLongID()));
						else b.setColor(ChatColor.WHITE)
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ChatComponentBuilder("Click to add user id").setColor(ChatColor.YELLOW).build()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+getMainCommandName()+" clients add "+u.getLongID()));
					}
					b.append(".").setColor(ChatColor.GRAY);
				}
				b.send(sender);
				return true;
			}
			public String getUsage() {return getName();}
		});
		subCommands.add(new SubCommand("groupslist","discordchat.admin.groupslist",SubCommandType.runnable,"Show Discord group list") {
			public List<String> onTabComplete(CommandSender sender, String[] args) {
				return new ArrayList<String>();
			}
			public boolean onCommand(CommandSender sender, String[] args) {
				ChatComponentBuilder b = PluginMain.getPrefixBuilder().append("Client List : ");
				for (IGuild g: PluginMain.discordClient.getGuilds()) {
					b.append("\n- ").setColor(ChatColor.GRAY).append(g.getName()).setColor(ChatColor.WHITE)
						.append(" : ").setColor(ChatColor.GRAY);
					boolean f = false;
					for (IRole r: g.getRoles()) {
						if(f) b.append(", ").setColor(ChatColor.GRAY);
						else f = true;
						b.append(r.getLongID()+" ("+r.getName()+")");
						if(PluginMain.groupsid.contains(String.valueOf(r.getLongID())))
							b.setColor(ChatColor.LIGHT_PURPLE)
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ChatComponentBuilder("Click to remove group id").setColor(ChatColor.YELLOW).build()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+getMainCommandName()+" groups del "+r.getLongID()));
						else b.setColor(ChatColor.WHITE)
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
									new ChatComponentBuilder("Click to add group id").setColor(ChatColor.YELLOW).build()))
							.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+getMainCommandName()+" groups add "+r.getLongID()));
					}
					b.append(".").setColor(ChatColor.GRAY);
				}
				b.send(sender);
				return true;
			}
			public String getUsage() {return getName();}
		});
	}

	public String getMainCommandName() {
		return name;
	}

	private List<SubCommand> subCommands;

	public static List<String> getTabCompletion(List<String> options, String[] args) {
		List<String> options_End = new ArrayList<String>();
		if (options.size() == 0)
			return options;
		if (args.length == 0)
			return options_End;
		String start = args[args.length - 1].toLowerCase();
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).toLowerCase().startsWith(start.toLowerCase()))
				options_End.add(options.get(i));
		}
		options_End.sort(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		}); // sort by name
		return options_End;
	}

	public SubCommand getSubCommandByName(String name) {
		for (SubCommand sc: subCommands) if(sc.getName().equalsIgnoreCase(name))return sc;
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		SubCommand sc;
		if(args.length>0 && (sc = getSubCommandByName(args[0]))!=null) {
			if(sc.getPermission()==null || (sc.getPermission()!=null && sender.hasPermission(sc.getPermission()))) {
				String[] scArgs = new String[args.length-1];
				System.arraycopy(args, 1, scArgs, 0, scArgs.length);
				if(!sc.onCommand(sender, scArgs))
					sender.sendMessage("/discordchat "+sc.getUsage());
			} else PluginMain.getPrefixBuilder().append("You haven't the permission to do that.")
				.setColor(ChatColor.RED).send(sender);
		} else {
			ChatComponentBuilder builder = PluginMain.getPrefixBuilder().append("-- Help DiscordChatBridge --").setColor(ChatColor.RED);
			for (SubCommand sch: subCommands) {
				if(!sender.hasPermission(sch.getPermission())) continue;
				HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
						new ChatComponentBuilder(sch.getType().equals(SubCommand.SubCommandType.runnable)?"Click to run command"
								:"Click to suggest command").setColor(ChatColor.YELLOW).build());
				ClickEvent ce = new ClickEvent((sch.getType().equals(SubCommand.SubCommandType.runnable)?ClickEvent.Action.RUN_COMMAND:
					ClickEvent.Action.SUGGEST_COMMAND), "/discordchat "+sch.getName()+(sch.getType().equals(SubCommand.SubCommandType.runnable)?"":" "));
				builder.append("\n/discordchat "+sch.getUsage()).setColor(ChatColor.GOLD).setClickEvent(ce).setHoverEvent(he)
					.append(" : ").setColor(ChatColor.GRAY).setClickEvent(ce).setHoverEvent(he)
					.append(sch.getDescription()).setColor(ChatColor.WHITE).setClickEvent(ce).setHoverEvent(he);
			}
			builder.send(sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> l = new ArrayList<String>();
		if(args.length>1) {
			SubCommand sc = getSubCommandByName(args[0]);
			if(sc!=null && sender.hasPermission(sc.getPermission())) {
				String[] scArgs = new String[args.length-1];
				System.arraycopy(args, 1, scArgs, 0, scArgs.length);
				return sc.onTabComplete(sender, scArgs);
			}
		} else {
			for (SubCommand sc: subCommands)
				l.add(sc.getName());
		}
		return getTabCompletion(l, args);
	}

}
