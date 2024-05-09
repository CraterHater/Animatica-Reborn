package animatica_reborn.craterhater.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerSettings {
	
	public static Map<UUID, PlayerSettings> playerSettingsLink = new HashMap<UUID, PlayerSettings>();
	
	public static PlayerSettings getPlayerSettings(Player p) {
		UUID uuid = p.getUniqueId();
		if(!playerSettingsLink.containsKey(uuid)) {
			PlayerSettings settings = new PlayerSettings();
			playerSettingsLink.put(uuid, settings);
			return settings;
		}
		
		return playerSettingsLink.get(uuid);
	}
	
	private Location position1 = null;
	private Location position2 = null;
	private String animation = null;
	public Map<String, Integer> insertionFrames = new HashMap<>();

	public String getAnimation() {
		return animation;
	}
	
	public void setAnimation(String animation) {
		this.animation = animation;
	}
	
	public Location getPosition1() {
		return position1;
	}
	
	public void setPosition1(Location position1) {
		this.position1 = position1;
		
		if(position2 != null) {
			ConfigurationListener.playCubeParticles(position1, position2, 0.25);
		}
	}
	
	public Location getPosition2() {
		return position2;
	}
	
	public void setPosition2(Location position2) {
		this.position2 = position2;
		
		if(position1 != null) {
			ConfigurationListener.playCubeParticles(position1, position2, 0.25);
		}
	}
}
