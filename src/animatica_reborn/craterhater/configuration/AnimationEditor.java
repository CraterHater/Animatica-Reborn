package animatica_reborn.craterhater.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import animatica_reborn.craterhater.animation.Animation;
import animatica_reborn.craterhater.commandhandler.MessageType;
import animatica_reborn.craterhater.datahandler.DataHandler;
import animatica_reborn.craterhater.datahandler.DataPath;
import animatica_reborn.craterhater.main.Standards;
import animatica_reborn.craterhater.ui.Citem;
import animatica_reborn.craterhater.ui.MasterUI;

public class AnimationEditor {
	
	public static void openMenu(Player p, String name, int page) {
		MasterUI ui = new MasterUI(ChatColor.WHITE+""+ChatColor.BOLD+"["+ChatColor.AQUA+""+ChatColor.BOLD+name+ChatColor.WHITE+""+ChatColor.BOLD+"]", 54, p);
		
		DataPath dataPath = new DataPath(name, "Animations");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		
		List<String> frames = fc.getStringList("Frames");
		
		PlayerSettings settings = PlayerSettings.getPlayerSettings(p);
		
		int insertionFrame = -1;
		if(settings.insertionFrames.containsKey(name)) {
			insertionFrame = settings.insertionFrames.get(name);
		}
		
		int slot = -1;
		for(int i = page * 45; i < (page+1) * 45 && i < frames.size(); i++) {
			slot++;
			
			String str = frames.get(i);
			
			List<String> lore = new ArrayList<>();
			lore.add("");
			
			int amount = 0;
			if(str.contains(">")) {
				amount = str.split(">").length;
			}else if(!str.equals("")) {
				amount = 1;
			}
			
			lore.add(ChatColor.WHITE+"Changes: " + ChatColor.YELLOW + amount);
			lore.add("");
			lore.add(ChatColor.WHITE+"Left-Click: " + ChatColor.YELLOW + "Paste Frame");
			lore.add(ChatColor.WHITE+"Right-Click: " + ChatColor.YELLOW + "Delete Frame");
			lore.add(ChatColor.WHITE+"Middle-Click: " + ChatColor.YELLOW + "Insert Frames Here");
			lore.add(ChatColor.WHITE+"Q (Drop-key): " + ChatColor.YELLOW + "Duplicate Frame");
			
			ItemStack is = new ItemStack(Material.WRITABLE_BOOK, 1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW+"Frame " + ChatColor.WHITE + "#" + (i+1));
			
			if(insertionFrame == i) {
				im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				im.addEnchant(Enchantment.DURABILITY, 1, true);
				
				lore.add("");
				lore.add(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+"Insertion Frame");
				lore.add(ChatColor.WHITE+""+ChatColor.ITALIC+"Frames will be inserted before this one");
			}
			
			im.setLore(lore);
			
			is.setItemMeta(im);
			
			final int frame = i;
			final int insertFrame = insertionFrame;
			ui.addItem(slot, is, new Citem() {
				public void call(MasterUI masterUI, ClickType clickType) {
					if(clickType.equals(ClickType.LEFT)) {
						Standards.sendMessage(p, "Pasted", "Loaded that frame", MessageType.GOOD);
						
						Animation animation = Animation.getAnimation(name);
						animation.stopPlaying();
						
						for(int i = 0; i <= frame; i++) {
							animation.pasteFrame(i);
						}
					}else if(clickType.equals(ClickType.RIGHT)) {
						Standards.sendMessage(p, "Deleted", "Deleted that frame", MessageType.GOOD);
						
						frames.remove(frame);
						fc.set("Frames", frames);
						DataHandler.saveFile(fc, dataPath);
						
						openMenu(p, name, page);
					}else if(clickType.equals(ClickType.MIDDLE)) {
						if(insertFrame == frame) {
							Standards.sendMessage(p, "Reset", "Reset the inserion frame. Frames will now again be added at the end.", MessageType.GOOD);
							PlayerSettings settings = PlayerSettings.getPlayerSettings(p);
							settings.insertionFrames.remove(name);
							openMenu(p, name, page);
							return;
						}
						
						PlayerSettings settings = PlayerSettings.getPlayerSettings(p);
						settings.insertionFrames.put(name, frame);
						Standards.sendMessage(p, "Set", "Set the insertion point to " + frame + " meaning all frames will now be inserted BEFORE that frame", MessageType.GOOD);
						openMenu(p, name, page);
					}else if(clickType.equals(ClickType.DROP)) {
						frames.add(frame, str);
						fc.set("Frames", frames);
						DataHandler.saveFile(fc, dataPath);
						
						openMenu(p, name, page);
					}
				}
			});
		}
		
		if(page > 0) {
			ui.addItem(45, Material.ARROW, ChatColor.GRAY+"Previous", new Citem() {
				public void call(MasterUI masterUI, ClickType clickType) {
					openMenu(p, name, page - 1);
				}
			});
		}
		
		if((page+1) * 45 < frames.size()) {
			ui.addItem(53, Material.ARROW, ChatColor.GRAY+"Next", new Citem() {
				public void call(MasterUI masterUI, ClickType clickType) {
					openMenu(p, name, page + 1);
				}
			});
		}
	}
}
