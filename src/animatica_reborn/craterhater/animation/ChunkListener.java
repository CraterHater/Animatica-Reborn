package animatica_reborn.craterhater.animation;

import java.io.File;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import animatica_reborn.craterhater.datahandler.DataHandler;
import animatica_reborn.craterhater.datahandler.DataPath;

public class ChunkListener implements Listener {
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		Chunk c = event.getChunk();
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
				
				if(Integer.parseInt(parts[0]) == c.getX() && Integer.parseInt(parts[1]) == c.getZ() && parts[2].equalsIgnoreCase(c.getWorld().getName())) {
					Animation animation = Animation.getAnimation(name);
					animation.startPlaying();
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		Chunk c = event.getChunk();
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
				
				if(Integer.parseInt(parts[0]) == c.getX() && Integer.parseInt(parts[1]) == c.getZ() && parts[2].equalsIgnoreCase(c.getWorld().getName())) {
					Animation animation = Animation.getAnimation(name);
					animation.stopPlaying();
					break;
				}
			}
		}
	}
}
