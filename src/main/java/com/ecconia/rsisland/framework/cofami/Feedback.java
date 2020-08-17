package com.ecconia.rsisland.framework.cofami;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Sends a formatted message to a ConsoleSender.
 * The format is: Prefix + normal message. Arguments in a different color.

 * @author Ecconia
 */
public class Feedback
{
	private final String prefix;
	
	private ChatColor normalPrimary = ChatColor.GRAY;
	private ChatColor normalSecondary = ChatColor.GOLD;
	
	private ChatColor errorPrimary = ChatColor.RED;
	private ChatColor errorSecondary = ChatColor.DARK_PURPLE;
	
	/**
	 * Create the Feedback class, the prefix has to be set, it will be placed before each feedback.
	 * 
	 * @param prefix - Plugin prefix, will be used in each feedback.
	 */
	public Feedback(String prefix)
	{
		Validate.notNull(prefix);
		
		this.prefix = prefix;
	}
	
	/**
	 * Send formatted normal message to CommandSender.
	 * Normal messages - Color grey, Highlight color gold.
	 * 
	 * @param sender - Receiver of the message.
	 * @param message - Message to be formatted.
	 * @param args - Arguments, '%v' in the message will be replaced with these.
	 */
	public void n(CommandSender sender, String message, Object... args)
	{
		if(sender != null)
		{
			sender.sendMessage(n(message, args));
		}
	}
	
	/**
	 * Send formatted error message to CommandSender.
	 * Error messages - Color red, Highlight color violet.
	 * 
	 * @param sender - Receiver of the message.
	 * @param message - Message to be formatted.
	 * @param args - Arguments, '%v' in the message will be replaced with these.
	 */
	public void e(CommandSender sender, String message, Object... args)
	{
		if(sender != null)
		{
			sender.sendMessage(e(message, args));
		}
	}
	
	/**
	 * Formats a normal message.
	 * Normal messages - Color grey, Highlight color gold.
	 * 
	 * @param message - Message to be formatted.
	 * @param args - Arguments, '%v' in the message will be replaced with these.
	 */
	public String n(String message, Object... args)
	{
		return format(message, normalPrimary, normalSecondary, args);
	}
	
	/**
	 * Formats an error message.
	 * Error messages - Color red, Highlight color violet.
	 * 
	 * @param message - Message to be formatted.
	 * @param args - Arguments, '%v' in the message will be replaced with these.
	 */
	public String e(String message, Object... args)
	{
		return format(message, errorPrimary, errorSecondary, args);
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
			Object obj = args[i++-1];
			if(obj == null)
			{
				obj = "'null'";
			}
			
			formatted += highlighColor + obj.toString() + color;
		}
		
		return prefix + color + formatted;
	}
	
	/**
	 * Change the colors which appear in chat for normal feedback.
	 * 
	 * @param primary - The text color of the feedback.
	 * @param secondary - The highlight color.
	 */
	public void setNormalColors(ChatColor primary, ChatColor secondary)
	{
		normalPrimary = primary;
		normalSecondary = secondary;
	}
	
	/**
	 * Change the colors which appear in chat for error feedback.
	 * 
	 * @param primary - The text color of the feedback.
	 * @param secondary - The highlight color.
	 */
	public void setErrorColors(ChatColor primary, ChatColor secondary)
	{
		errorPrimary = primary;
		errorSecondary = secondary;
	}
	
	/**
	 * Create a simple prefix format: "[Pluginname] ".
	 * 
	 * @param bracketColor - The color of the backets
	 * @param textColor - The color of the pluginname
	 * @param name - The pluginname
	 * @return string - A prefix which can be used for the Feedback object 
	 */
	public static String simplePrefix(ChatColor bracketColor, ChatColor textColor, String name)
	{
		return bracketColor + "[" + textColor + name + bracketColor + "] ";
	}
}
