package com.ecconia.rsisland.framework.cofami;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

/**
 * Create a {@link Subcommand} which can hold other Subcommands.<br>
 * It will route the execution to the registered subcommand in charge.<br>
 * It sends the available subcommands to the sender on tab complete.<br>
 * <br>
 * Can be overwritten, if itself should have some custom execution.<br>
 */
public class GroupSubcommand extends Subcommand
{
	private final Map<String, Subcommand> subcommands;

	public GroupSubcommand(String name, Subcommand... subcommands)
	{
		super(name);

		this.subcommands = new HashMap<>();
		for(Subcommand subcommand : subcommands)
		{
			this.subcommands.put(subcommand.getName(), subcommand);
		}
	}

	@Override
	protected boolean hasCallRequirements()
	{
		return false;
	}

	@Override
	public String getPermissions()
	{
		Set<String> permissions = new HashSet<>();
		if(hasCallRequirements())
		{
			permissions.add(super.getPermissions());
		}
		for(Subcommand subcommand : subcommands.values())
		{
			String subPermission = subcommand.getPermissions();
			if(subPermission != null)
			{
				permissions.add(subPermission);
			}
		}

		if(permissions.isEmpty())
		{
			return null;
		}

		return StringUtils.join(permissions, ";");
	}
	
	@Override
	protected boolean hasPermission(CommandSender sender)
	{
		if(hasCallRequirements())
		{
			return sender.hasPermission(super.getPermissions());
		}
		else
		{
			for(Subcommand s : subcommands.values())
			{
				if(s.hasPermission(sender))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	@Override
	protected boolean isType(CommandSender sender)
	{
		if(hasCallRequirements())
		{
			return super.isType(sender);
		}
		else
		{
			for(Subcommand s : subcommands.values())
			{
				if(s.isType(sender))
				{
					return true;
				}
			}
			return false;
		}
	}

	@Override
	protected void init(Feedback f, String path, String permission)
	{
		super.init(f, path, permission);

		for(Subcommand subcommmand : subcommands.values())
		{
			subcommmand.init(f, super.path + " ", permission + getName() + ".");
		}
	}

	@Override
	public void exec(CommandSender sender, String[] arguments)
	{
		if(arguments.length == 0)
		{
			exec(sender);
			return;
		}
		
		Subcommand subcommand = subcommands.get(arguments[0]);
		if(subcommand == null)
		{
			noMatch(sender, arguments);
			return;
		}
		
		subcommand.exec(sender, Arrays.copyOfRange(arguments, 1, arguments.length));
	}
	
	protected void exec(CommandSender sender)
	{
		//TODO custom Message?
		f.n(sender, "Use tabcomplete to get a list of subcomands.");
	}
	
	protected void noMatch(CommandSender sender, String[] arguments)
	{
		//TODO: custom Message?
		f.e(sender, "No such subcommand: %v", arguments[0]);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] arguments)
	{
		if(arguments.length == 1)
		{
			String typed = arguments[0];
			
			return subcommands.values().stream().filter(subcommand -> {
				return subcommand.hasPermission(sender) && subcommand.isType(sender);
			}).map(subcommand -> {
				return subcommand.getName();
			}).filter(subcommandName -> {
				return StringUtils.startsWithIgnoreCase(subcommandName, typed);
			}).collect(Collectors.toList());
		}
		else
		{
			String typed = arguments[0];
			Subcommand subCommand = subcommands.get(typed);
			if(subCommand != null && subCommand.hasPermission(sender) && subCommand.isType(sender))
			{
				return subCommand.onTabComplete(sender, Arrays.copyOfRange(arguments, 1, arguments.length));
			}
		}
		
		return Collections.emptyList();
	}
}
