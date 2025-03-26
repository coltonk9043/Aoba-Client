/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractPathManager {
	protected static MinecraftClient MC = MinecraftClient.getInstance();
	protected BlockPos target;

	protected boolean avoidWater = false;
	protected boolean avoidLava = true;

	public AbstractPathManager() {

	}

	public AbstractPathManager(BlockPos target) {
		this.target = target;
	}

	/**
	 * Gets the current target of the path manager.
	 * 
	 * @return Current target.
	 */
	public BlockPos getTarget() {
		return target;
	}

	/**
	 * Sets the current target of the path manager.
	 * 
	 * @param target Target to set to.
	 */
	public void setTarget(BlockPos target) {
		this.target = target;
	}

	/**
	 * Gets whether or not the pathfinder will avoid water.
	 * 
	 * @return Whether or not the pathfinder will avoid water.
	 */
	public boolean getAvoidWater() {
		return avoidWater;
	}

	/**
	 * Sets whether the pathfinder will avoid water.
	 * 
	 * @param state State to set whether or not pathfinder will avoid water.
	 */
	public void setAvoidWater(boolean state) {
		avoidWater = state;
	}

	/**
	 * Gets whether or not the pathfinder will avoid lava.
	 * 
	 * @return Whether or not the pathfinder will avoid lava.
	 */
	public boolean getAvoidLava() {
		return avoidLava;
	}

	/**
	 * Sets whether the pathfinder will avoid lava.
	 * 
	 * @param state State to set whether or not pathfinder will avoid lava.
	 */
	public void setAvoidLava(boolean state) {
		avoidLava = state;
	}

	/**
	 * Recalculates the path taken by the current pathfinder to reach the target
	 * destination.
	 * 
	 * @param pos Target position.
	 * @return The list of Path Nodes that represent the path taken to the
	 *         destination.
	 */
	public abstract ArrayList<PathNode> recalculatePath(BlockPos pos);

	/**
	 * Heuristic function to calculate the value used to find the shortest path.
	 * 
	 * @param position Current position.
	 * @param target   Target position.
	 * @return The value used to calculate the shortest path.
	 */
	protected abstract float heuristic(PathNode position, BlockPos target);

	/**
	 * Reconstructs a path from a map of nodes and their parent node.
	 * 
	 * @param start     Start position
	 * @param target    Target position.
	 * @param parentMap Map of parent-child relation
	 * @return List of PathNodes that represents the reconstructed path.
	 */
	protected ArrayList<PathNode> reconstructPath(PathNode start, PathNode target,
			HashMap<PathNode, PathNode> parentMap) {
		// Add target.
		ArrayList<PathNode> path = new ArrayList<>();
		PathNode prev = target;
		path.addFirst(prev);

		// If the start equals the target, return early since there is only one node.
		if (start.equals(target))
			return path;

		// Otherwise traverse the parentMap until we reach the destination.
		// Skips any nodes that are in the same 'direction' as the previous to trim it.
		PathNode current = parentMap.get(prev);
		BlockPos delta = prev.pos.subtract(current.pos);
		while (!current.equals(start)) {
			PathNode next = parentMap.get(current);
			if (!current.pos.subtract(next.pos).equals(delta))
				path.addFirst(current);
			prev = current;
			current = next;
			delta = prev.pos.subtract(current.pos);
		}

		// Add the start position and return the path.
		path.addFirst(start);
		return path;
	}

	/**
	 * Returns the neighbouring blocks used to traverse the world.
	 * 
	 * @param node Node to get the neighbour blocks from.
	 * @return The list of neighbouring blocks from a node.
	 */
	protected abstract ArrayList<PathNode> getNeighbouringBlocks(PathNode node);

	/**
	 * Determines whether a player can pass through a block position.
	 * 
	 * @param pos Position to check.
	 * @return Whether or not the player can pass through a block position.
	 */
	protected static boolean isPlayerPassable(BlockPos pos) {
		return checkBodyAndHeadPos(pos);
	}

	/**
	 * Determines whether a player can pass diagonally from a position to another
	 * position.
	 * 
	 * @param playerPos Player's current position.
	 * @param pos       Position to check.
	 * @return whether or not the player can pass diagonally through a block
	 *         position.
	 */
	protected static boolean isPlayerPassableDiagonal(BlockPos playerPos, BlockPos pos) {
		// Calculate the difference in coordinates
		int dx = playerPos.getX() - pos.getX();
		int dz = playerPos.getZ() - pos.getZ();

		// Determine neighboring positions
		BlockPos adjacentPosX = (dx < 0) ? pos.west() : (dx > 0) ? pos.east() : null;
		BlockPos adjacentPosZ = (dz < 0) ? pos.north() : (dz > 0) ? pos.south() : null;

		// Check if both adjacent positions are valid and if the current and adjacent
		// positions are passable
		return adjacentPosX != null && adjacentPosZ != null && checkBodyAndHeadPos(pos)
				&& checkBodyAndHeadPos(adjacentPosX) && checkBodyAndHeadPos(adjacentPosZ);
	}

	/**
	 * Function that checks whether a player will fit inside of a block pos from
	 * their feet up.
	 * 
	 * @param feetPos Feet position to check.
	 * @return Whether or not the player will fit into a space from the feet up.
	 */
	protected static boolean checkBodyAndHeadPos(BlockPos feetPos) {
		BlockPos headPos = feetPos.up();
		BlockState state = MC.world.getBlockState(feetPos);
		BlockState stateUp = MC.world.getBlockState(headPos);
		return !state.isSolid() && !stateUp.isSolid();
	}
}

/**
 * Represents an object used to store a node and it's cost from the destination.
 */
class PathFinderEntry {
	public PathNode node;
	public float cost;

	public PathFinderEntry(PathNode node, float cost) {
		this.node = node;
		this.cost = cost;
	}
}
