package com.ecconia.rsisland.framework.cofami;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
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
	public CommandHandler(JavaPlugin plugin, Feedback f, Subcommand mainCommand)
	{
		Validate.notNull(mainCommand);
		Validate.notNull(f);
		Validate.notNull(plugin);
		
		this.mainCommand = mainCommand;
		this.f = f;

		mainCommand.init(f, "/", "");

		PluginCommand command = plugin.getCommand(mainCommand.getName());

		//TODO: custom?
		command.setPermissionMessage(ChatColor.RED + "You do not have permission to use this command.");
		command.setPermission(mainCommand.getPermissions());
		command.setExecutor(this);
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
			sender.sendMessage(e.getMessage());
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments)
	{
		return mainCommand.onTabComplete(sender, arguments);
	}
}
