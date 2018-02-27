package com.ecconia.rsisland.framework.cofami;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Feedback
{
	private final String prefix;
	
	public Feedback(String prefix)
	{
		this.prefix = prefix;
	}
	
	public void n(CommandSender sender, String message, Object... args)
	{
		if(sender != null)
		{
			sender.sendMessage(n(message, args));
		}
	}
	
	public void e(CommandSender sender, String message, Object... args)
	{
		if(sender != null)
		{
			sender.sendMessage(e(message, args));
		}
	}
	
	public String n(String message, Object... args)
	{
		return format(message, ChatColor.GRAY, ChatColor.GOLD, args);
	}
	
	public String e(String message, Object... args)
	{
		return format(message, ChatColor.RED, ChatColor.DARK_PURPLE, args);
	}
	
	private String format(String message, ChatColor color, ChatColor highlighColor, Object... args)
	{
		String parts[] = message.split("%v");

		String formatted = parts[0];
		for(int i = 1; i < parts.length; i++)
		{
			formatted += highlighColor + args[i-1].toString() + color;
			formatted += parts[i];
		}
		
		int i = parts.length;
		
		while(args.length >= i)
		{
			formatted += highlighColor + args[i++-1].toString() + color;
		}
		
		return prefix + color + formatted;
	}
}
