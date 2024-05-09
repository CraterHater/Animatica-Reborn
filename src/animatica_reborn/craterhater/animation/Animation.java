package animatica_reborn.craterhater.animation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import animatica_reborn.craterhater.datahandler.DataHandler;
import animatica_reborn.craterhater.datahandler.DataPath;
import animatica_reborn.craterhater.main.Main;

public class Animation {

	public static Map<String, Animation> animationLink = new HashMap<>();
	
	public static Animation getAnimation(String name) {
		if(animationLink.containsKey(name)) {
			return animationLink.get(name);
		}
		
		return new Animation(name);
	}
	
	private String name;
	private boolean isPlaying = false;
	private boolean isPaused = false;
	
	public Animation(String name) {
		this.name = name;
		animationLink.put(name, this);
	}
	
	public void startPlaying() {
		if(isPlaying){
			return;
		}
		
		DataPath dataPath = new DataPath(name, "Animations");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		
		if(fc.getBoolean("Reverse") && fc.getStringList("Frames").size() != fc.getStringList("ReverseFrames").size()) {
			FrameHandler.generateReverseFrames(name);
		}
		
		isPaused = false;
		isPlaying = true;
		
		playFrame(0);
	}
	
	public void playFrame(int frame) {
		if(!isPlaying) {
			return;
		}
		
		if(isPaused) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable(){
	            public void run(){
	              playFrame(frame);
	            }
	        }, 1);
			
			return;
		}
		
		DataPath dataPath = new DataPath(name, "Animations");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		
		if(!fc.contains("Frames")) {
			stopPlaying();
			return;
		}
		
		List<String> frames = null;
		
		boolean reverse = fc.getBoolean("Reverse");
		
		if(reverse) {
			frames = fc.getStringList("ReverseFrames");
		}else {
			frames = fc.getStringList("Frames");
		}
		
		int interval = fc.getInt("Interval");
		
		if(frames.size() > frame) {
			FrameHandler.pasteFrame(frames.get(frame));
		}else {
			if(fc.getInt("Mode") == 1) {
				//Repeat
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable(){
		            public void run(){
		            	playFrame(0);
		            }
		        }, interval);
				return;
			}else if(fc.getInt("Mode") == 2) {
				//Switch
				fc.set("Reverse", !reverse);
				DataHandler.saveFile(fc, dataPath);
				
				if(fc.getBoolean("Reverse") && fc.getStringList("Frames").size() != fc.getStringList("ReverseFrames").size()) {
					FrameHandler.generateReverseFrames(name);
				}
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable(){
		            public void run(){
		            	playFrame(0);
		            }
		        }, interval);
				return;
			}
			
			stopPlaying();
			return;
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable(){
            public void run(){
            	playFrame(frame + 1);
            }
        }, interval);
	}
	
	public void pasteFrame(int frame) {
		DataPath dataPath = new DataPath(name, "Animations");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		List<String> frames = fc.getStringList("Frames");
		if(frames.size() > frame) {
			FrameHandler.pasteFrame(frames.get(frame));
		}
	}
	
	public boolean isPaused() {
		return isPaused;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}
	
	public void stopPlaying() {
		isPaused = false;
		isPlaying = false;
	}
}
