package animatica_reborn.craterhater.animation;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import animatica_reborn.craterhater.datahandler.DataTypeHandler;

public class FrameBlock {

	private Location l;
	private BlockData data;
	
	public FrameBlock(Location l, BlockData data) {
		this.l = l;
		this.data = data;
	}
	
	public String serialize() {
		return DataTypeHandler.serializeLocation(l)+";"+l.getBlock().getBlockData().getAsString();
	}
	
	public Location getLocation() {
		return l;
	}
	public void setL(Location l) {
		this.l = l;
	}
	public BlockData getData() {
		return data;
	}
	public void setData(BlockData data) {
		this.data = data;
	}
}
