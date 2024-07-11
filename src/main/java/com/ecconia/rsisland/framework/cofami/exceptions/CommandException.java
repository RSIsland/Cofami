package com.ecconia.rsisland.framework.cofami.exceptions;

public class CommandException extends RuntimeException
{
	private Object[] args;
	
	public CommandException(String message, Object... args)
	{
		super(message);
		
		this.args = args;
	}
	
	public Object[] getArgs()
	{
		return args;
	}
}
