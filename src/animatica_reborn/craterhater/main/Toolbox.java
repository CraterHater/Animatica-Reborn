package animatica_reborn.craterhater.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Toolbox {

	public static boolean isNumeric(String s) {
		try {
			Double.parseDouble(s);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public static ItemStack getItem(Material m, String title, String... lore) {
		ItemStack is = new ItemStack(m);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(title);
		
		List<String> list = new ArrayList<String>();
		for(String s : lore) {
			list.add(s);
		}
		
		im.setLore(list);
		is.setItemMeta(im);
		return is;
	}
	
	public static boolean worldExists(String world) {
		for(World w : Bukkit.getWorlds()) {
			if(w.getName().equals(world)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static BlockFace getCardinalDirection(Location loc) {
		double rotation = (loc.getYaw() - 90.0F) % 360.0F;

		if (rotation < 0.0D) {
		rotation += 360.0D;
		}
		if ((0.0D <= rotation) && (rotation < 45.0D))
		return BlockFace.WEST;
		if ((45.0D <= rotation) && (rotation < 135.0D))
		return BlockFace.NORTH;
		if ((135.0D <= rotation) && (rotation < 225.0D))
		return BlockFace.EAST;
		if ((225.0D <= rotation) && (rotation < 315.0D))
		return BlockFace.SOUTH;
		if ((315.0D <= rotation) && (rotation < 360.0D)) {
		return BlockFace.WEST;
		}
		return null;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double diff(double one, double two) {
		double three = one-two;
		if(three < 0) {
			three *= -1;
		}
		return three;
	}
	
	public static Float getRandom(double rangeMin, double rangeMax) {
		Random r = new Random();
		return (float)(rangeMin + (rangeMax - rangeMin) * r.nextDouble());
	}
	
	public static String getEnumeration(List<String> list) {
		try {
			String header = "";
			
			if(!list.isEmpty()) {
				for(int i = 0; i < list.size(); i++) {
					if(list.size() == (i+1)) {header += ChatColor.translateAlternateColorCodes('&', " &7and ");}
					if(list.size() != (i+1)) {header += ChatColor.translateAlternateColorCodes('&', "&7, ");}
					
					header += ChatColor.translateAlternateColorCodes('&', "&f")+capitalizeWords(list.get(i).toLowerCase());
				}
				
				if(list.size() > 1) {
					header = header.replaceFirst(ChatColor.translateAlternateColorCodes('&', "&7, "), "");
				}else {
					header = header.replaceFirst(ChatColor.translateAlternateColorCodes('&', " &7and "), "");
				}
				
				return header;
			}
			
			return null;
		}catch(Exception e) {
			ErrorHandler.handleError(e);
			return null;
		}
	}
	
	public static String capitalizeWords(String text) {
		try {
			text = text.toLowerCase();
		    StringBuilder sb = new StringBuilder();
		    if(text.length()>0){
		        sb.append(Character.toUpperCase(text.charAt(0)));
		    }
		    for (int i=1; i<text.length(); i++){
		        String chPrev = String.valueOf(text.charAt(i-1));
		        String ch = String.valueOf(text.charAt(i));
	
		        if(Objects.equals(chPrev, " ")){
		            sb.append(ch.toUpperCase());
		        }else {
		            sb.append(ch);
		        }
	
		    }
	
		    return sb.toString();
		}catch(Exception e) {
			ErrorHandler.handleError(e);
			return text;
		}
	}
}
