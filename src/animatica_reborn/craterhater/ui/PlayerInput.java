package animatica_reborn.craterhater.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import animatica_reborn.craterhater.main.Main;

public class PlayerInput implements Listener {

	public static Map<UUID, PlayerInput> playerLink = new HashMap<>();
	
	private Player player;
	private Citem2 call;
	
	public PlayerInput(Player p, Citem2 call) {
		if(playerLink.containsKey(p.getUniqueId())) {	
			PlayerInput input = playerLink.get(p.getUniqueId());
			HandlerList.unregisterAll(input);
		}
		
		playerLink.put(p.getUniqueId(), this);
		Bukkit.getPluginManager().registerEvents(this, Main.main);
		this.player = p;
		this.call = call;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		
		if(!p.equals(player)) {
			return;
		}
		
		event.setCancelled(true);
		
		HandlerList.unregisterAll(this);
		
		if(event.getMessage().equalsIgnoreCase("cancel")) {
			p.sendMessage(ChatColor.GREEN+"Cancelled!");
			return;
		}
		
		new BukkitRunnable() {
	        @Override
	        public void run() {
	        	call.call(event.getMessage());
	        }
	     }.runTask(Main.main);
	}
}
	