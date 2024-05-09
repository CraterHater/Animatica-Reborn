package animatica_reborn.craterhater.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import animatica_reborn.craterhater.main.Main;

public class PlayerInputLocation implements Listener {

	public static Map<UUID, PlayerInputLocation> playerLink = new HashMap<>();
	
	private Player player;
	private Citem3 call;
	
	public PlayerInputLocation(Player p, Citem3 call) {
		if(playerLink.containsKey(p.getUniqueId())) {	
			PlayerInputLocation input = playerLink.get(p.getUniqueId());
			HandlerList.unregisterAll(input);
		}
		
		playerLink.put(p.getUniqueId(), this);
		Bukkit.getPluginManager().registerEvents(this, Main.main);
		this.player = p;
		this.call = call;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if(!p.equals(player)) {
			return;
		}
		
		event.setCancelled(true);

		playerLink.remove(p.getUniqueId());
		HandlerList.unregisterAll(this);
		
	    call.call(p.getLocation());
	}
}
	