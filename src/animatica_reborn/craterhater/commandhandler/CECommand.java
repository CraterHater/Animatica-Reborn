package animatica_reborn.craterhater.commandhandler;

import org.bukkit.command.CommandSender;

import animatica_reborn.craterhater.main.Toolbox;

public class CECommand {

	private String description;
	private String permission;
	private String chapter;
	private boolean isOP;
	private Action action;
	private Argument[] arguments;
	private boolean console;
	private int page;
	
	public CECommand(String description, String permission, String chapter, boolean isOP, boolean console, int page, Action action, Argument... arguments) {
		this.description = description;
		this.permission = permission;
		this.chapter = chapter;
		this.isOP = isOP;
		this.action = action;
		this.arguments = arguments;
		this.console = console;
		this.page = page;
	}
	
	public String getHelp() {
		return description;
	}
	
	public boolean canConsole() {
		return console;
	}
	
	public ArgumentType getArgumentType(int argument) {
		if(arguments.length > argument) {
			return arguments[argument].getType();
		}
		
		return null;
	}
	
	public String getChapter() {
		return chapter;
	}
	
	public boolean isOP() {
		return isOP;
	}
	
	public int getPage() {
		return page;
	}
	
	public String getLabel(int argument) {
		if(arguments.length > argument) {
			return arguments[argument].getLabel();
		}
		
		return null;
	}
	
	public String getDescription(int argument) {
		if(arguments.length > argument) {
			return arguments[argument].getDescription();
		}
		
		return null;
	}
	
	public int getArgumentCount() {
		return arguments.length;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean checkPlayerExecutes(CommandSender sender, String[] args) {
		boolean valid = true;
		
		if(args.length != arguments.length) {
			valid = false;
		}else {
			for(int i = 0; i < arguments.length; i++) {
				Argument argument = arguments[i];
				if(argument.getType() == ArgumentType.LABEL) {
					if(!args[i].equalsIgnoreCase(argument.getLabel())) {
						valid = false;
					}
				}
				
				if(argument.getType() == ArgumentType.STRING) {
					if(Toolbox.isNumeric(args[i])) {
						valid = false;
					}
				}
				
				if(argument.getType() == ArgumentType.INTEGER) {
					if(!Toolbox.isNumeric(args[i])) {
						valid = false;
					}
				}
				
				if(argument.getType() == ArgumentType.BOOLEAN) {
					if(!args[i].equalsIgnoreCase("true") && !args[i].equalsIgnoreCase("false")) {
						valid = false;
					}
				}
			}
		}
		
		if(valid) {
			playerExecutes(sender, args);
		}
		
		return valid;
	}
	
	private void playerExecutes(CommandSender p, String[] args) {
		action.call(p, args);
	}
}
