package animatica_reborn.craterhater.commandhandler;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import animatica_reborn.craterhater.animation.Animation;
import animatica_reborn.craterhater.datahandler.DataHandler;
import animatica_reborn.craterhater.datahandler.DataPath;

public class CommandBlock implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof BlockCommandSender) {
			if(args.length > 1) {
				if(args[0].equalsIgnoreCase("play")) {
					String name = args[1];
					Animation animation = Animation.getAnimation(name);
					animation.startPlaying();
				}else if(args[0].equalsIgnoreCase("stop")) {
					String name = args[1];
					Animation animation = Animation.getAnimation(name);
					animation.stopPlaying();
				}else if(args[0].equalsIgnoreCase("pause")) {
					String name = args[1];
					Animation animation = Animation.getAnimation(name);
					animation.setPaused(true);
				}else if(args[0].equalsIgnoreCase("resume")) {
					String name = args[1];
					Animation animation = Animation.getAnimation(name);
					animation.setPaused(false);
				}else if(args[0].equalsIgnoreCase("reverse")) {
					String name = args[1];
					
					if(args.length > 2) {
						DataPath dataPath = new DataPath(name, "Animations");
						FileConfiguration fc = DataHandler.getFile(dataPath);
						fc.set("Reverse", args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("1"));
						DataHandler.saveFile(fc, dataPath);
					}
				}else if(args[0].equalsIgnoreCase("interval")) {
					String name = args[1];
					
					if(args.length > 2 && Integer.parseInt(args[2]) > 0) {
						DataPath dataPath = new DataPath(name, "Animations");
						FileConfiguration fc = DataHandler.getFile(dataPath);
						fc.set("Interval", Integer.parseInt(args[2]));
						DataHandler.saveFile(fc, dataPath);
					}
				}else if(args[0].equalsIgnoreCase("mode")) {
					String name = args[1];
					
					if(args.length > 2) {
						DataPath dataPath = new DataPath(name, "Animations");
						FileConfiguration fc = DataHandler.getFile(dataPath);
						fc.set("Mode", Integer.parseInt(args[2]));
						DataHandler.saveFile(fc, dataPath);
					}
				}
			}
			
		}
		
		return true;
	}
}
