package animatica_reborn.craterhater.animation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import animatica_reborn.craterhater.datahandler.DataHandler;
import animatica_reborn.craterhater.datahandler.DataPath;
import animatica_reborn.craterhater.datahandler.DataTypeHandler;

public class FrameHandler {
	
	public static void addFrame(DataPath dataPath, int insertion, boolean reverse) {
		FileConfiguration fc = DataHandler.getFile(dataPath);
		
		String frames = "Frames";
		if(reverse) {
			frames = "ReverseFrames";
		}
		
		List<FrameBlock> blocks = new ArrayList<FrameBlock>(); 
		if(fc.contains(frames) && fc.isList(frames)) {
			int c = 0;
			for(String frame : fc.getStringList(frames)) {
				c++;
				if(insertion != -1 && c > insertion) {
					break;
				}
				
				for(String s : frame.split(">")) {
					if(s.length() <= 0) {continue;}
					String[] split = s.split(";");
					Location l = DataTypeHandler.unserializeLocation(split[0]);
					BlockData data = Bukkit.createBlockData(split[1]);
					
					//Check if block already exists in the previous frame.
					for(int i = 0; i < blocks.size(); i++) {
						FrameBlock oldBlock = blocks.get(i);
						if(oldBlock.getLocation().equals(l)) {
							blocks.remove(i);
							i--;
						}
					}
					
					blocks.add(new FrameBlock(l, data));
				}
			}
		}
		
		String newFrame = "";
		
		Location point1 = DataTypeHandler.unserializeLocation(fc.getString("Location1"));
		Location point2 = DataTypeHandler.unserializeLocation(fc.getString("Location2"));
		Vector max = Vector.getMaximum(point1.toVector(), point2.toVector());
		Vector min = Vector.getMinimum(point1.toVector(), point2.toVector());
		for (int i = min.getBlockX(); i <= max.getBlockX();i++) {
			for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
				for (int k = min.getBlockZ(); k <= max.getBlockZ();k++) {
					Block block = point1.getWorld().getBlockAt(i, j, k);
					
					boolean found = false;
					
					//Check if block already exists in the previous frame.
					for(FrameBlock oldBlock : blocks) {
						if(oldBlock.getLocation().equals(block.getLocation()) && oldBlock.getData().equals(block.getBlockData())) {
							found = true;
							break;
						}
					}
					
					if(found) {
						continue;
					}
					
					newFrame += new FrameBlock(block.getLocation(), block.getBlockData()).serialize() + ">";
				}
			}
		}
		
		if(newFrame.contains(">")) {
			newFrame = removeLastChar(newFrame);
		}
		
		List<String> framesList = fc.getStringList(frames);
		
		if(insertion == -1) {
			framesList.add(newFrame);
		}else {
			framesList.add(insertion, newFrame);
		}
		
		fc.set(frames, framesList);
		DataHandler.saveFile(fc, dataPath);
	}
	
	public static void generateReverseFrames(String name) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE+"Animatica reborn generated reverse frames for animation " + name);
		
		DataPath dataPath = new DataPath(name, "Animations");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		fc.set("ReverseFrames", new ArrayList<String>());
		DataHandler.saveFile(fc, dataPath);
		
		List<String> frames = fc.getStringList("Frames");
		
		for(int i = 0; i < frames.size(); i++) {
			for(int f = 0; f < frames.size() - i; f++) {
				FrameHandler.pasteFrame(frames.get(f));
			}
			
			FrameHandler.addFrame(dataPath, -1, true);
		}
	}
	
	private static String removeLastChar(String str) {
		return str.trim().substring(0, str.length() - 1);
	}
	
	public static void pasteFrame(String frame) {
		for(String s : frame.split(">")) {
			if(s.length() <= 0) {continue;}
			String[] split = s.split(";");
			Location l = DataTypeHandler.unserializeLocation(split[0]);
			BlockData data = Bukkit.createBlockData(split[1]);
			l.getBlock().setBlockData(data);
		}
	}
}
