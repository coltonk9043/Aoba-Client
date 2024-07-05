package net.aoba.pathfinding;

import net.minecraft.util.math.BlockPos;

public class PathNode {
	public final BlockPos pos;
	private boolean wasJump = false;
	
	public PathNode(int x, int y, int z) {
		this.pos = new BlockPos(x, y, z);
	}
	
	public PathNode(BlockPos pos) {
		this.pos = pos;
	}
	
	public boolean getWasJump() {
		return this.wasJump;
	}
	
	public void setWasJump(boolean state) {
		this.wasJump = state;
	}
	
	@Override
	public int hashCode(){
		int x = pos.getX();
		int z = pos.getZ();
		return x & 0xFF | (x & Short.MAX_VALUE) << 8 | (z & Short.MAX_VALUE) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? Integer.MAX_VALUE : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(obj instanceof PathNode) {
			PathNode path = (PathNode)obj;
			return path.pos.getX() == this.pos.getX() &&
					path.pos.getY() == this.pos.getY() &&
					path.pos.getZ() == this.pos.getZ();
		}else
			return false;
	}
}
