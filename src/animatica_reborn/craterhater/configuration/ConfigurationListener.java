package animatica_reborn.craterhater.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import animatica_reborn.craterhater.commandhandler.MessageType;
import animatica_reborn.craterhater.main.Standards;

public class ConfigurationListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		ItemStack is = event.getItem();
		
		if(is == null) {
			return;
		}
		
		if(!is.hasItemMeta()) {
			return;
		}
		
		if(!is.getItemMeta().getDisplayName().equals(Standards.wandName)) {
			return;
		}
		
		Player p = event.getPlayer();
		
		Action action = event.getAction();
		if(action == Action.LEFT_CLICK_BLOCK) {
			Standards.sendMessage(p, "Set", "Location one was set!", MessageType.GOOD);
			
			Location l = event.getClickedBlock().getLocation();
			PlayerSettings.getPlayerSettings(p).setPosition1(l);
			event.setCancelled(true);
			return;
		}
		
		if(action == Action.RIGHT_CLICK_BLOCK) {
			Standards.sendMessage(p, "Set", "Location two was set!", MessageType.GOOD);
			
			Location l = event.getClickedBlock().getLocation();
			PlayerSettings.getPlayerSettings(p).setPosition2(l);
			event.setCancelled(true);
			return;
		}
		
		PlayerSettings settings = PlayerSettings.getPlayerSettings(p);
		
		if(settings.getPosition1() != null && settings.getPosition2() != null) {
			playCubeParticles(settings.getPosition1(), settings.getPosition2(), 0.25);
		}
	}
	
	public static void playCubeParticles(Location corner1, Location corner2, double particleDistance) {
		if(corner1.getWorld() == null) {
			return;
		}
		
		if(corner1.getWorld() != corner2.getWorld()) {
			return;
		}
		
		if(corner1.distance(corner2) > 500) {
			return;
		}
		
		List<Location> list = getHollowCube(corner1, corner2, particleDistance);
		for(Location l : list) {
			l.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY(), l.getZ(), 1, 0, 0, 0);
		}
	}
	
	public static List<Location> getHollowCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<Location>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;
     
        for (double x = minX; x <= maxX; x+=particleDistance) {
            for (double y = minY; y <= maxY; y+=particleDistance) {
                for (double z = minZ; z <= maxZ; z+=particleDistance) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
     
        return result;
    }
}
