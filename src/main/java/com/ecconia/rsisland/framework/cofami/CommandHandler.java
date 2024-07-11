package com.ecconia.rsisland.framework.cofami;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.ecconia.rsisland.framework.cofami.exceptions.CommandException;
import com.ecconia.rsisland.framework.cofami.exceptions.NoPermissionException;
import com.ecconia.rsisland.framework.cofami.exceptions.WrongTypeException;

/**
 * The CommandHandler connects the BukkitAPI with a {@link Subcommand}.
 * It registers the command and its permissions, including subcommands.
 * 
 * @author Ecconia
 */
public class CommandHandler implements CommandExecutor, TabCompleter
{
	private final Subcommand mainCommand;
	private final Feedback f;
	
	/**
	 * The CommandHandler connects the BukkitAPI with the Subcommand provided as parameter.
	 * It also registers the command and sets its permissions.
	 * 
	 * @param plugin - The plugin, used to get the plugin command. 
	 * @param f - The formatting class {@link Feedback}.
	 * @param mainCommand - The main {@link Subcommand}.
	 */
	public CommandHandler(Plugin plugin, Feedback f, Subcommand mainCommand)
	{
		this.mainCommand = mainCommand;
		this.f = f;
		
		mainCommand.init(f, "/", "");
		
		init(plugin.getServer().getPluginCommand(mainCommand.getName()));
	}
	
	/**
	 * The CommandHandler connects the BukkitAPI with the Subcommand provided as parameter.
	 * It also registers the command and sets its permissions.
	 * 
	 * @param plugin - The plugin, used to get the plugin command. 
	 * @param f - The formatting class {@link Feedback}.
	 * @param mainCommand - The main {@link Subcommand}.
	 */
	public CommandHandler(JavaPlugin plugin, Feedback f, Subcommand mainCommand)
	{
		this.mainCommand = mainCommand;
		this.f = f;
		
		mainCommand.init(f, "/", "");
		
		init(plugin.getCommand(mainCommand.getName()));
	}
	
	private void init(PluginCommand command)
	{
		//TODO: custom?
		command.setPermissionMessage(ChatColor.RED + "You do not have permission to use this command.");
		command.setPermission(mainCommand.getPermissions());
		command.setExecutor(this);
		command.setTabCompleter(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments)
	{
		try
		{
			mainCommand.exec(sender, arguments);
		}
		catch(WrongTypeException e)
		{
			//TODO: Custom message.
			f.e(sender, "You have the wrong sender type to execute this command.");
		}
		catch(NoPermissionException e)
		{
			//TODO: Custom message.
			f.e(sender, "You do not have permission to use %v.", e.getMessage());
		}
		catch(CommandException e)
		{
			f.e(sender, e.getMessage(), e.getArgs());
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments)
	{
		return mainCommand.onTabComplete(sender, arguments);
	}
	
	/**
	 * Register many commands with one method
	 * 
	 * @param plugin - Plugin which is used to register the commands
	 * @param f - Feedback object used by the commands
	 * @param subcommands - The commands to register
	 */
	public static void register(JavaPlugin plugin, Feedback f, Subcommand... subcommands)
	{
		for(Subcommand subcommand : subcommands)
		{
			new CommandHandler(plugin, f, subcommand);
		}
	}
}
