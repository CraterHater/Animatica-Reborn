package animatica_reborn.craterhater.datahandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DataTypeHandler {

	public static List<String> serializeUUIDList(List<UUID> uuids){
		List<String> list = new ArrayList<>();
		for(UUID uuid : uuids) {
			list.add(uuid.toString());
		}
		return list;
	}
	
	public static List<UUID> unserializeUUIDList(List<String> uuids){
		List<UUID> list = new ArrayList<>();
		for(String uuid : uuids) {
			list.add(UUID.fromString(uuid));
		}
		return list;
	}
	
	public static String serializeLocation(Location l) {
		return l.getWorld().getName()+","+l.getX()+","+l.getY()+","+l.getZ()+","+l.getPitch()+","+l.getYaw();
	}
	
	public static Location unserializeLocation(String s) {
		String[] parts = s.split(",");
		Location l = new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
		l.setPitch(Float.parseFloat(parts[4]));
		l.setYaw(Float.parseFloat(parts[5]));
		return l;
	}
}
