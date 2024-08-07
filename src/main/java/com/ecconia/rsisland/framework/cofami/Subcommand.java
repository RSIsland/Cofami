package com.ecconia.rsisland.framework.cofami;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.ecconia.rsisland.framework.cofami.exceptions.CommandException;
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
		return !((onlyConsole && !(sender instanceof ConsoleCommandSender)) || (onlyPlayer && !(sender instanceof Player)));
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
	 * @param sender - The CommandSender executing this command.
	 * @param arguments - The arguments used on this subcommand.
	 */
	public abstract void exec(CommandSender sender, final String[] arguments);
	
	public List<String> onTabComplete(CommandSender sender, final String[] args)
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
	
	/**
	 * Ease of use, for the ones who like this method from other languages ;)
	 * Ends the command with an error message.
	 * 
	 * @param message Error message
	 * @param args Arguments to be inserted/appended to the error message. Will replace '%v'.
	 */
	protected void die(String message, Object... args)
	{
		throw new CommandException(message, args);
	}
}
