package net.aoba.cmd;

public abstract class CommandException extends Exception{
	private static final long serialVersionUID = 1L;
	protected Command cmd;
	
	public CommandException(Command cmd) {
		this.cmd = cmd;
	}
	
	public abstract void PrintToChat();
}
