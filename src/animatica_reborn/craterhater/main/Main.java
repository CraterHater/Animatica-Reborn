package animatica_reborn.craterhater.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import animatica_reborn.craterhater.animation.Animation;
import animatica_reborn.craterhater.animation.ChunkListener;
import animatica_reborn.craterhater.commandhandler.CommandBlock;
import animatica_reborn.craterhater.commandhandler.MasterCommand;
import animatica_reborn.craterhater.configuration.ConfigurationListener;
import animatica_reborn.craterhater.datahandler.DataHandler;
import animatica_reborn.craterhater.datahandler.DataPath;

public class Main extends JavaPlugin {

	public static Main main;

	public void onEnable() {
		Main.main = this;

		saveDefaultConfig();
		loadCommands();
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new ConfigurationListener(), main);
		manager.registerEvents(new ChunkListener(), main);

		Standards.MAIN_PERMISSION = getConfig().getString("main_permission");

		if (getConfig().getString("show_startup_message").equalsIgnoreCase("true")) {
			Bukkit.getConsoleSender().sendMessage("");
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "              [" + Standards.PLUGIN_NAME + "]");
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "              Loaded - Succesfully");
			Bukkit.getConsoleSender().sendMessage("");
		}
	
		getCommand("cmd_animatica").setExecutor(new CommandBlock());
		
		startAnimations();
	}

	private void startAnimations() {
		for(File file : DataHandler.getAllFilesInDirectory("Animations")) {
			String name = file.getName().replaceAll(".yml", "");
			DataPath dataPath = new DataPath(name, "Animations");
			FileConfiguration fc = DataHandler.getFile(dataPath);
			
			if(!fc.contains("Chunks")) {
				continue;
			}
				
			List<String> list = fc.getStringList("Chunks");
			
			for(String s : list) {
				String[] parts = s.split(";");
				
				boolean worldExists = false;
				String world = parts[2];
				for(World w : Bukkit.getWorlds()) {
					if(w.getName().equalsIgnoreCase(world)) {
						worldExists = true;
						break;
					}
				}
				
				if(!worldExists) {
					continue;
				}
				
				Chunk c = Bukkit.getWorld(world).getChunkAt(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				if(c.isLoaded()) {
					Animation animation = Animation.getAnimation(name);
					animation.startPlaying();
					break;
				}
			}
		}
	}
	
	private void loadCommands() {
		FileConfiguration configurationFile = getConfig();

		List<String> aliases = new ArrayList<>();
		aliases.add(configurationFile.getString("main_command"));
		for (String alias : configurationFile.getStringList("command_aliases")) {
			aliases.add(alias);
		}

		Standards.COMMAND_ALIASES = aliases.toArray(new String[0]);

		MasterCommand command = new MasterCommand(configurationFile.getString("main_command"), "/<command> [args]",
				"Used to control " + Standards.PLUGIN_NAME, aliases);
		command.register();
		command.loadCommands();
	}
}
