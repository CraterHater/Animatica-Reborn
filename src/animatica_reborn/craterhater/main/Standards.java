package animatica_reborn.craterhater.main;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import animatica_reborn.craterhater.commandhandler.MessageType;

public class Standards {

	public static final String PLUGIN_NAME = "Animatica Reborn";
	
	public static String[] COMMAND_ALIASES = {};
	
	public static final String DATA_FILE = "player_data";
	public static final String LANGUAGE_FILE = "Messages";
	
	public static final String[] COMMAND_CATEGORIES = {"Administrative", "Setup", "Animation", "Control", "Modes"};
	
	public static String MAIN_PERMISSION = "animatica";
	public static String wandName = ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"["+ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+"Animatica Wand"+ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"]";
	
	public static void sendMessage(Player p, String verb, String message, MessageType type) {
		if(!message.endsWith(".") && !message.endsWith("?") && !message.endsWith("!")) {
			message = message + ".";
		}
		p.sendMessage(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.AQUA+""+ChatColor.BOLD+"Animatica"+ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+"Reborn"+ChatColor.WHITE+""+ChatColor.BOLD+"] " + ChatColor.YELLOW + Toolbox.capitalizeWords(verb) + " - " + ChatColor.RESET + message);
	
		switch(type) {
		case BAD:
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, Toolbox.getRandom(0.2, 1.2));
			break;
		case GOOD:
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, Toolbox.getRandom(0.2, 1.2));
			break;
		default:
			break;
		}
	}
}
