package animatica_reborn.craterhater.commandhandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import animatica_reborn.craterhater.animation.Animation;
import animatica_reborn.craterhater.animation.FrameHandler;
import animatica_reborn.craterhater.configuration.AnimationEditor;
import animatica_reborn.craterhater.configuration.ConfigurationListener;
import animatica_reborn.craterhater.configuration.PlayerSettings;
import animatica_reborn.craterhater.datahandler.DataHandler;
import animatica_reborn.craterhater.datahandler.DataPath;
import animatica_reborn.craterhater.datahandler.DataTypeHandler;
import animatica_reborn.craterhater.main.ErrorHandler;
import animatica_reborn.craterhater.main.Standards;
import animatica_reborn.craterhater.main.Toolbox;
import animatica_reborn.craterhater.ui.Citem;
import animatica_reborn.craterhater.ui.MasterUI;

public class MasterCommand extends AbstractCommand {

	 public MasterCommand(String command, String usage, String description, List<String> aliases) {
        super(command, usage, description, null, aliases);
    }

	private ArrayList<CECommand> allCommands = new ArrayList<>();
	
	//String permission, String chapter, boolean isOP, boolean console, int page, Action action, Argument... arguments
	
	public void loadCommands() {	
		allCommands.clear();
		
		allCommands.add(new CECommand("Shows commands on that page", Standards.MAIN_PERMISSION+".help",Standards.COMMAND_CATEGORIES[0],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					int page = (int)Double.parseDouble(arguments[1]);
					if(page < 1) {page = 1;}
					int maxPages = getMaxPages();
					if(page > maxPages) {page = maxPages;}
					
					sendHelpMessage(p, page);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("help"), new Argument(ArgumentType.INTEGER, "page")));
		
		allCommands.add(new CECommand("Get the selection wand", Standards.MAIN_PERMISSION+".wand",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;
				q.getInventory().addItem(Toolbox.getItem(Material.STONE_AXE, Standards.wandName, "", ChatColor.WHITE+""+ChatColor.BOLD+"Left-Click: " + ChatColor.RESET + "" + ChatColor.AQUA + "position 1", ChatColor.WHITE+""+ChatColor.BOLD+"Right-Click: " + ChatColor.RESET + "" + ChatColor.AQUA + "position 2"));
				Standards.sendMessage(q, "Wand", "Left and right click to set your selection boundaries.", MessageType.GOOD);
			}
		}, new Argument("wand")));
		
		allCommands.add(new CECommand("Create new animation", Standards.MAIN_PERMISSION+".new",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;
				
				String name = arguments[1];
				DataPath dataPath = new DataPath(name, "Animations");
				if(DataHandler.checkIfFileExists(dataPath)) {
					Standards.sendMessage(q, "Failed", "That animation already exists", MessageType.BAD);
					return;
				}
				
				PlayerSettings settings = PlayerSettings.getPlayerSettings(q);
				if(settings.getPosition1() == null) {
					Standards.sendMessage(q, "Failed", "Position 1 (left-click with wand '/an wand') has not been set yet!", MessageType.BAD);
					return;
				}
				
				if(settings.getPosition2() == null) {
					Standards.sendMessage(q, "Failed", "Position 2 (left-click with wand '/an wand') has not been set yet!", MessageType.BAD);
					return;
				}
					
				Standards.sendMessage(q, "Success", "Created a new animation named " + arguments[1] + " and automatically selected it.", MessageType.GOOD);
				
				FileConfiguration fc = DataHandler.getFile(dataPath);
				fc.set("Frames", new ArrayList<String>());
				fc.set("Location1", DataTypeHandler.serializeLocation(settings.getPosition1()));
				fc.set("Location2", DataTypeHandler.serializeLocation(settings.getPosition2()));
				fc.set("Interval", 20);
				fc.set("Mode", 0);
				fc.set("Reverse", false);
				fc.set("ReverseFrames", new ArrayList<String>());
				DataHandler.saveFile(fc, dataPath);
				
				settings.setAnimation(name);
			}
		}, new Argument("new"), new Argument(ArgumentType.STRING, "name")));
		
		allCommands.add(new CECommand("Create new animation", Standards.MAIN_PERMISSION+".new",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				
				MasterUI ui = new MasterUI(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.RED+""+ChatColor.BOLD+"Delete?"+ChatColor.WHITE+""+ChatColor.BOLD+"]", 9, q);
				ui.addItem(2, Material.EMERALD_BLOCK, ChatColor.GREEN+""+ChatColor.BOLD+"Permanently Delete", new Citem() {
					public void call(MasterUI masterUI, ClickType clickType) {
						q.closeInventory();
						
						DataHandler.deleteFile(dataPath);
						Standards.sendMessage(q, "Success", "Deleted the animation.", MessageType.GOOD);
					}
				});
				
				ui.addItem(6, Material.REDSTONE_BLOCK, ChatColor.RED+""+ChatColor.BOLD+"Cancel", new Citem() {
					public void call(MasterUI masterUI, ClickType clickType) {
						q.closeInventory();
						Standards.sendMessage(q, "Cancelled", "The animation was NOT deleted.", MessageType.NEUTRAL);
					}
				});
			}
		}, new Argument("delete")));
		
		allCommands.add(new CECommand("Shows all animations", Standards.MAIN_PERMISSION+".list",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;
				
				q.sendMessage("");
				q.sendMessage(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.AQUA+""+ChatColor.BOLD+"All Animations"+ChatColor.WHITE+""+ChatColor.BOLD+"]");
				
				PlayerSettings settings = PlayerSettings.getPlayerSettings(q);
				
				List<File> list = DataHandler.getAllFilesInDirectory("Animations");
				int i = 0;
				for(File file : list) {
					i++;
					String name = file.getName().replaceAll(".yml", "");
					
					DataPath dataPath = new DataPath(name, "Animations");
					FileConfiguration fc = DataHandler.getFile(dataPath);
					
					String mode = "Stop";
					if(fc.getInt("Mode") == 1) {
						mode = "Repeat";
					}else if(fc.getInt("Mode") == 2) {
						mode = "Switch";
					}
					
					String reverse = "Normal";
					if(fc.getBoolean("Reverse")) {
						reverse = ChatColor.RED+"Reverse";
					}
					
					if(name.equalsIgnoreCase(settings.getAnimation())) {
						q.sendMessage(ChatColor.GRAY+"#"+ChatColor.WHITE+i+": " + ChatColor.YELLOW + name + ChatColor.AQUA + "*" + ChatColor.WHITE + " " + ChatColor.ITALIC+"Mode: " + mode + " Direction: " + reverse);
					}else {
						q.sendMessage(ChatColor.GRAY+"#"+ChatColor.WHITE+i+": " + ChatColor.YELLOW + name + ChatColor.WHITE + " " + ChatColor.ITALIC+"Mode: " + mode + " Direction: " + reverse);
					}
				}
				
				q.sendMessage(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.AQUA+""+ChatColor.BOLD+"============"+ChatColor.WHITE+""+ChatColor.BOLD+"]");
				q.sendMessage("");
			}
		}, new Argument("list")));
		
		allCommands.add(new CECommand("Teleports you to animation", Standards.MAIN_PERMISSION+".list",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;
				
				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				if(!fc.contains("Location1") || !fc.contains("Location2")) {
					Standards.sendMessage(q, "Corrupted", "That animation file is missing key fields. Please recreate the animation or fix what is broken in the file.", MessageType.BAD);
					return;
				}
				
				q.teleport(DataTypeHandler.unserializeLocation(fc.getString("Location1")));
				Standards.sendMessage(q, "Success", "Teleported you to the animation named '"+name+"'.", MessageType.GOOD);
			}
		}, new Argument("tp")));
		
		allCommands.add(new CECommand("Shows all animations", Standards.MAIN_PERMISSION+".select",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;
				
				String name = arguments[1];
				DataPath dataPath = new DataPath(name, "Animations");
				if(!DataHandler.checkIfFileExists(dataPath)) {
					Standards.sendMessage(q, "Failed", "That animation could not be found", MessageType.BAD);
					return;
				}
				
				PlayerSettings settings = PlayerSettings.getPlayerSettings(q);
				settings.setAnimation(name);
				Standards.sendMessage(q, "Selected", "Succesfully selected the animation", MessageType.GOOD);
				
				FileConfiguration fc = DataHandler.getFile(dataPath);
				if(!fc.contains("Location1") || !fc.contains("Location2")) {
					return;
				}
				
				ConfigurationListener.playCubeParticles(DataTypeHandler.unserializeLocation(fc.getString("Location1")), DataTypeHandler.unserializeLocation(fc.getString("Location2")), 0.25);
			}
		}, new Argument("select"), new Argument(ArgumentType.STRING, "name")));
		
		allCommands.add(new CECommand("Updates the selection", Standards.MAIN_PERMISSION+".selection",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;
				
				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				
				PlayerSettings settings = PlayerSettings.getPlayerSettings(q);
				if(settings.getPosition1() == null) {
					Standards.sendMessage(q, "Failed", "Position 1 (left-click with wand '/an wand') has not been set yet!", MessageType.BAD);
					return;
				}
				
				if(settings.getPosition2() == null) {
					Standards.sendMessage(q, "Failed", "Position 2 (left-click with wand '/an wand') has not been set yet!", MessageType.BAD);
					return;
				}
					
				Standards.sendMessage(q, "Success", "Changed the positions! Only what is between these two points will be included in the animation!", MessageType.GOOD);
				
				FileConfiguration fc = DataHandler.getFile(dataPath);
				fc.set("Location1", DataTypeHandler.serializeLocation(settings.getPosition1()));
				fc.set("Location2", DataTypeHandler.serializeLocation(settings.getPosition2()));
				DataHandler.saveFile(fc, dataPath);
				
				ConfigurationListener.playCubeParticles(DataTypeHandler.unserializeLocation(fc.getString("Location1")), DataTypeHandler.unserializeLocation(fc.getString("Location2")), 0.25);
			}
		}, new Argument("selection")));
		
		allCommands.add(new CECommand("Add frame", Standards.MAIN_PERMISSION+".addFrame",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				if(!fc.contains("Location1") || !fc.contains("Location2")) {
					Standards.sendMessage(q, "Corrupted", "That animation file is missing key fields. Please recreate the animation or fix what is broken in the file.", MessageType.BAD);
					return;
				}
				
				FrameHandler.addFrame(dataPath, -1, false);
				Standards.sendMessage(q, "Added", "Succesfully added a frame to this animation", MessageType.GOOD);
			}
		}, new Argument("frame"), new Argument("add")));
		
		allCommands.add(new CECommand("Inserts frame", Standards.MAIN_PERMISSION+".insertFrame",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				if(!fc.contains("Location1") || !fc.contains("Location2")) {
					Standards.sendMessage(q, "Corrupted", "That animation file is missing key fields. Please recreate the animation or fix what is broken in the file.", MessageType.BAD);
					return;
				}
				
				PlayerSettings settings = PlayerSettings.getPlayerSettings(q);
				if(!settings.insertionFrames.containsKey(name)) {
					Standards.sendMessage(q, "Not found", "You have not set an insertion frame. Use /an frame editor, then middle-click a frame to select it as your insertion frame.", MessageType.BAD);
					return;
				}
				
				FrameHandler.addFrame(dataPath, settings.insertionFrames.get(name), false);
				settings.insertionFrames.put(name, settings.insertionFrames.get(name)+1);
				Standards.sendMessage(q, "Added", "Succesfully inserted a frame into this animation", MessageType.GOOD);
			}
		}, new Argument("frame"), new Argument("insert")));
		
		allCommands.add(new CECommand("Clear all frames", Standards.MAIN_PERMISSION+".clear",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				if(!fc.contains("Location1") || !fc.contains("Location2")) {
					Standards.sendMessage(q, "Corrupted", "That animation file is missing key fields. Please recreate the animation or fix what is broken in the file.", MessageType.BAD);
					return;
				}
				
				MasterUI ui = new MasterUI(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.RED+""+ChatColor.BOLD+"Delete all frames?"+ChatColor.WHITE+""+ChatColor.BOLD+"]", 9, q);
				ui.addItem(2, Material.EMERALD_BLOCK, ChatColor.GREEN+""+ChatColor.BOLD+"Permanently Delete", new Citem() {
					public void call(MasterUI masterUI, ClickType clickType) {
						q.closeInventory();
						
						fc.set("Frames", new ArrayList<String>());
						DataHandler.saveFile(fc, dataPath);
						
						Standards.sendMessage(q, "Deleted", "Succesfully cleared all frames from this animation", MessageType.GOOD);
					}
				});
				
				ui.addItem(6, Material.REDSTONE_BLOCK, ChatColor.RED+""+ChatColor.BOLD+"Cancel", new Citem() {
					public void call(MasterUI masterUI, ClickType clickType) {
						q.closeInventory();
						Standards.sendMessage(q, "Cancelled", "The animation frames were NOT deleted.", MessageType.NEUTRAL);
					}
				});
			}
		}, new Argument("frame"), new Argument("clear")));
		
		allCommands.add(new CECommand("Open a visual editor", Standards.MAIN_PERMISSION+".editor",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				AnimationEditor.openMenu(q, name, 0);
			}
		}, new Argument("frame"), new Argument("editor")));
		
		allCommands.add(new CECommand("Sets frame interval", Standards.MAIN_PERMISSION+".interval",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				int value = Integer.parseInt(arguments[1]);
				
				if(value <= 0) {
					Standards.sendMessage(q, "Failed", "That's an invalid value.", MessageType.BAD);
					return;
				}
				
				fc.set("Interval", value);
				DataHandler.saveFile(fc, dataPath);
				
				Standards.sendMessage(q, "Success", "Set the interval to " + value + " ticks. This equals " + value/20 + " seconds between frames", MessageType.GOOD);
			}
		}, new Argument("interval"), new Argument(ArgumentType.INTEGER, "ticks")));
		
		allCommands.add(new CECommand("Make animation repeat", Standards.MAIN_PERMISSION+".mode",Standards.COMMAND_CATEGORIES[4],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				Standards.sendMessage(q, "Success", "Set the new mode to 'Repeat'. After finishing, the animation will automatically restart.", MessageType.GOOD);
				fc.set("Mode", 1);
				DataHandler.saveFile(fc, dataPath);
			}
		}, new Argument("mode"), new Argument("repeat")));
		
		allCommands.add(new CECommand("Make animation switch", Standards.MAIN_PERMISSION+".mode",Standards.COMMAND_CATEGORIES[4],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				Standards.sendMessage(q, "Success", "Set the new mode to 'Switch'. When the animation finishes it will replay in the other direction.", MessageType.GOOD);
				fc.set("Mode", 2);
				DataHandler.saveFile(fc, dataPath);
			}
		}, new Argument("mode"), new Argument("switch")));
		
		allCommands.add(new CECommand("Make animation mode stop", Standards.MAIN_PERMISSION+".mode",Standards.COMMAND_CATEGORIES[4],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				Standards.sendMessage(q, "Success", "Set the new mode to 'Stop'. When the animation finishes it will simply stop", MessageType.GOOD);
				fc.set("Mode", 0);
				DataHandler.saveFile(fc, dataPath);
			}
		}, new Argument("mode"), new Argument("stop")));
		
		allCommands.add(new CECommand("Make animation reverse", Standards.MAIN_PERMISSION+".reverse",Standards.COMMAND_CATEGORIES[4],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				fc.set("Reverse", !fc.getBoolean("Reverse"));
				DataHandler.saveFile(fc, dataPath);
				
				if(fc.getBoolean("Reverse")) {
					Standards.sendMessage(q, "Success", "Set this animation to play in reverse", MessageType.GOOD);
					FrameHandler.generateReverseFrames(name);
				}else {
					Standards.sendMessage(q, "Success", "Set this animation to play normally", MessageType.GOOD);
				}
			}
		}, new Argument("reverse")));
		
		allCommands.add(new CECommand("Plays the animation", Standards.MAIN_PERMISSION+".play",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				Animation animation = Animation.getAnimation(name);
				
				if(animation.isPlaying()) {
					Standards.sendMessage(q, "Failed", "That animation is already playing.", MessageType.BAD);
				}else {
					Standards.sendMessage(q, "Success", "Started playing that animation.", MessageType.GOOD);
				}
				
				animation.startPlaying();
			}
		}, new Argument("play")));
		
		allCommands.add(new CECommand("Pauses the animation", Standards.MAIN_PERMISSION+".pause",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				Animation animation = Animation.getAnimation(name);
				
				if(animation.isPaused()) {
					Standards.sendMessage(q, "Failed", "That animation is already paused.", MessageType.BAD);
				}else {
					Standards.sendMessage(q, "Success", "Paused the animation.", MessageType.GOOD);
				}
				
				animation.setPaused(true);
			}
		}, new Argument("pause")));
		
		allCommands.add(new CECommand("Pauses the animation", Standards.MAIN_PERMISSION+".pause",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				Animation animation = Animation.getAnimation(name);
				
				if(!animation.isPaused()) {
					Standards.sendMessage(q, "Failed", "That animation is not paused.", MessageType.BAD);
				}else {
					Standards.sendMessage(q, "Success", "Resumed the animation.", MessageType.GOOD);
				}
				
				animation.setPaused(false);
			}
		}, new Argument("resume")));
		
		allCommands.add(new CECommand("Stops the animation", Standards.MAIN_PERMISSION+".stop",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				Animation animation = Animation.getAnimation(name);
				
				if(!animation.isPlaying()) {
					Standards.sendMessage(q, "Failed", "That animation is not playing.", MessageType.BAD);
				}else {
					Standards.sendMessage(q, "Success", "Stopped playing that animation.", MessageType.GOOD);
				}
				
				animation.stopPlaying();
			}
		}, new Argument("stop")));
		
		allCommands.add(new CECommand("When this chunk loads, start the animation", Standards.MAIN_PERMISSION+".chunk",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				List<String> list = new ArrayList<>();
				if(fc.contains("Chunks")) {
					list = fc.getStringList("Chunks");
				}
				
				Chunk chunk = q.getLocation().getChunk();
				list.add(chunk.getX()+";"+chunk.getZ()+";"+chunk.getWorld().getName());
				fc.set("Chunks", list);
				DataHandler.saveFile(fc, dataPath);
				
				Standards.sendMessage(q, "Success", "Added this chunk to the list of animation activators", MessageType.GOOD);
			}
		}, new Argument("chunk"), new Argument("add")));
		
		allCommands.add(new CECommand("Show list of activator chunks", Standards.MAIN_PERMISSION+".chunk",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				List<String> list = new ArrayList<>();
				if(fc.contains("Chunks")) {
					list = fc.getStringList("Chunks");
				}
				
				q.sendMessage("");
				q.sendMessage(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.AQUA+""+ChatColor.BOLD+"All Chunks"+ChatColor.WHITE+""+ChatColor.BOLD+"]");
				
				int i = 0;
				for(String s : list) {
					i++;
					q.sendMessage(ChatColor.GRAY+"#"+ChatColor.WHITE+i+": " + ChatColor.YELLOW + s);
				}
				
				q.sendMessage(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.AQUA+""+ChatColor.BOLD+"============"+ChatColor.WHITE+""+ChatColor.BOLD+"]");
				q.sendMessage("");
			}
		}, new Argument("chunk"), new Argument("list")));
		
		allCommands.add(new CECommand("Show list of activator chunks", Standards.MAIN_PERMISSION+".chunk",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				if(!(p instanceof Player)) {
					return;
				}
				
				Player q  = (Player)p;

				String name = getSelectedAnimation(q);
				if(name == null) {
					return;
				}
				
				DataPath dataPath = new DataPath(name, "Animations");
				FileConfiguration fc = DataHandler.getFile(dataPath);
				
				List<String> list = new ArrayList<>();
				if(fc.contains("Chunks")) {
					list = fc.getStringList("Chunks");
				}
				
				Chunk chunk = q.getLocation().getChunk();
				String str = chunk.getX()+";"+chunk.getZ()+";"+chunk.getWorld().getName();
				
				if(!list.contains(str)) {
					Standards.sendMessage(q, "Failed", "This chunk is not an activator chunk", MessageType.BAD);
					return;
				}
				
				list.remove(str);
				fc.set("Chunks", list);
				DataHandler.saveFile(fc, dataPath);
				
				Standards.sendMessage(q, "Success", "Removed this chunk from the list of animation activators", MessageType.GOOD);
			}
		}, new Argument("chunk"), new Argument("remove")));
	}
	
	public String getSelectedAnimation(Player p) {
		PlayerSettings settings = PlayerSettings.getPlayerSettings(p);
		
		String name = settings.getAnimation();
		if(name == null) {
			Standards.sendMessage(p, "Failed", "You have not selected any animation. Use /an select <name>, to do so.", MessageType.BAD);
			return null;
		}
		
		DataPath dataPath = new DataPath(name, "Animations");
		if(!DataHandler.checkIfFileExists(dataPath)) {
			Standards.sendMessage(p, "Failed", "That animation could not be found", MessageType.BAD);
			return null;
		}
		
		return name;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		boolean isCommandValid = false;
		
		for(String viableCommand : Standards.COMMAND_ALIASES) {
			if(!viableCommand.equalsIgnoreCase(commandLabel)) {
				continue;
			}
			isCommandValid = true;
			break;
		}
		
		if(!isCommandValid) {
			return true;
		}
		
		if(args.length == 0) {
			sendHelpMessage(sender, 1);
			return true;
		}
		
		boolean valid = false;
		for(CECommand command : allCommands) {
			if(!command.canConsole() && !(sender instanceof Player)) {
				continue;
			}
			
			if(command.isOP() && !sender.isOp() && !sender.hasPermission(command.getPermission()) && !sender.hasPermission(Standards.MAIN_PERMISSION+".*")) {
				sender.sendMessage(ChatColor.RED+"Invalid Permissions");
				break;
			}
			
			if(!command.checkPlayerExecutes(sender, args)) {
				continue;
			}
			
			valid = true;
			break;
		}
	
		if(!valid) {
			sendHelpMessage(sender, 1);
		}
		
		return true;
	}
	
	private void sendHelpMessage(CommandSender p, int page) {		
		boolean console = true;
		if(p instanceof Player) {
			console = false;
		}
		
		p.sendMessage("");
		p.sendMessage(ChatColor.GOLD+"                   " + ChatColor.UNDERLINE + "" + Standards.PLUGIN_NAME);
		p.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"    All commands for " + Standards.PLUGIN_NAME + " are displayed here.");
		p.sendMessage(ChatColor.GRAY+""+page+"/"+getMaxPages());
		p.sendMessage("");
		
		String chapter = null;
		
		int ID = 0;
		for(CECommand command : allCommands) {
			if(command.getPage() != page) {
				continue;
			}
			
			if(command.isOP() && !p.isOp() && !p.hasPermission(command.getPermission()) && !p.hasPermission(Standards.MAIN_PERMISSION+".*")) {
				continue;
			}
			
			if(console && !command.canConsole()) {
				continue;
			}
			
			if(chapter == null || !chapter.equals(command.getChapter())){
				p.sendMessage("");
				p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+command.getChapter());
				chapter = command.getChapter();
			}
			
			ID++;
			
			String allArguments = "";
			for(int i = 0; i < command.getArgumentCount(); i++) {
				if(command.getArgumentType(i) == ArgumentType.LABEL) {
					allArguments += " " + command.getLabel(i);
				}else {
					allArguments += " " + "<"+command.getDescription(i)+">";
				}
			}
			
			p.sendMessage(ChatColor.GRAY+"#"+ChatColor.WHITE+ID+": /" + ChatColor.YELLOW + Standards.COMMAND_ALIASES[0] + ChatColor.GRAY + allArguments + ChatColor.DARK_GRAY + " " + ChatColor.ITALIC + command.getHelp());
		}
	}
	
	public int getMaxPages() {
		int maxPages = 0;
		for(CECommand command : allCommands) {
			if(command.getPage() > maxPages) {
				maxPages = command.getPage();
			}
		}
		return maxPages;
	}
}

