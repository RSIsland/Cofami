package com.ecconia.rsisland.framework.cofami;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.ecconia.rsisland.framework.cofami.exceptions.NoPermissionException;
import com.ecconia.rsisland.framework.cofami.exceptions.WrongTypeException;

public abstract class Subcommand
{
	protected Feedback f;
	protected String path;
	protected String name;

	private String permission;
	
	private boolean onlyConsole;
	private boolean onlyPlayer;

	public Subcommand(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String getPermissions()
	{
		return permission;
	}
	
	protected void onlyPlayer()
	{
		onlyPlayer = true;
	}
	
	protected void onlyConsole()
	{
		onlyConsole = true;
	}

	//#########################################################################

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
