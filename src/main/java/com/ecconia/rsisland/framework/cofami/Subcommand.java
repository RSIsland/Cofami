package com.ecconia.rsisland.framework.cofami;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.ecconia.rsisland.framework.cofami.exceptions.NoPermissionException;
import com.ecconia.rsisland.framework.cofami.exceptions.WrongTypeException;

/**
 * A subcommand can be used as main-command in a {@link CommandHandler}, but also as actual subcommand in a {@link GroupSubcommand}.
 * 
 * The permission (if enabled) is the name(label) of the command.
 * 
 * @author ecconia
 */
public abstract class Subcommand
{
	protected Feedback f;
	protected String path;
	protected String name;

	private String permission;
	
	private boolean onlyConsole;
	private boolean onlyPlayer;

	/**
	 * 
	 * @param name - The name/label of this command {@code /<name>}
	 */
	public Subcommand(String name)
	{
		Validate.notNull(name);
		
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Internal method.<br>
	 * Permission string set for the main command<br>
	 * 
	 * @return The permission if one, else permission1;permission2
	 */
	public String getPermissions()
	{
		return permission;
	}
	
	/**
	 * Will only allow this command to be used by Players.<br>
	 * Prevents tabcompleting of this command for other CommandSenders.<br>
	 * <br>
	 * To prevent the execution call {@code checkType(sender)} in exec<br>
	 * <br>
	 * If used together with onlyConsole(), both are allowed.<br>
	 */
	protected void onlyPlayer()
	{
		onlyPlayer = true;
	}
	
	/**
	 * Will only allow this command to be used by Console.<br>
	 * Prevents tabcompleting of this command for other CommandSenders.<br>
	 * <br>
	 * To prevent the execution call {@code checkType(sender)} in exec<br>
	 * <br>
	 * If used together with onlyPlayer(), both are allowed.<br>
	 */
	protected void onlyConsole()
	{
		onlyConsole = true;
	}

	//#########################################################################

	/**
	 * Overwrite this method to remove/add permissions to this subcommand.<br>
	 * 
	 * Default for {@link Subcommand}s is true - Permission will be set.<br>
	 * Default for {@link GroupSubcommand}s is false - Permission will not be set.<br>
	 * 
	 * @return boolean - if permissions should be set and onlyPlayer/Console be used in tabcomplete.
	 */
	protected boolean hasCallRequirements()
	{
		return true;
	}
	
	protected boolean isType(CommandSender sender)
	{
		if(onlyConsole || onlyPlayer)
		{
			boolean isConsole = sender instanceof ConsoleCommandSender;
			boolean isPlayer = sender instanceof Player;
			
			if((onlyConsole && onlyPlayer && !(isConsole || isPlayer)) || (onlyConsole && !isConsole) || (onlyPlayer && !isPlayer))
			{
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean hasPermission(CommandSender sender)
	{
		if(permission == null)
		{
			return true;
		}
		else
		{
			return sender.hasPermission(permission);
		}
	}

	/**
	 * Gets called on execution of this command.
	 * 
	 * @param sender
	 * @param arguments
	 */
	public abstract void exec(CommandSender sender, String[] arguments);

	public List<String> onTabComplete(CommandSender sender, String[] args)
	{
		return Collections.emptyList();
	}

	//#########################################################################

	protected void init(Feedback f, String path, String permission)
	{
		this.f = f;

		this.path = path + name;
		if(hasCallRequirements())
		{
			this.permission = permission + name;
		}
	}

	//#########################################################################

	protected Player getPlayer(CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			throw new WrongTypeException();
		}
		
		return (Player) sender;
	}
	
	protected void checkType(CommandSender sender)
	{
		if(!isType(sender))
		{
			throw new WrongTypeException();
		}
	}

	protected void checkPermission(CommandSender sender)
	{
		if(permission != null && !sender.hasPermission(permission))
		{
			throw new NoPermissionException(path);
		}
	}
}
