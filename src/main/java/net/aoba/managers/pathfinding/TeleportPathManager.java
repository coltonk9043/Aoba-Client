/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

/**
 * The WalkingPathManager class is responsible for managing and calculating
 * walking paths in the game. It extends the AbstractPathManager and provides
 * implementations for path recalculation and heuristic calculation.
 */
public class TeleportPathManager extends AbstractPathManager {

	protected float radius = 5.0f;

	public float getRadius() {
		return radius;
	}

	public void setRadius(float newRadius) {
		radius = newRadius;
	}

	@Override
	public ArrayList<PathNode> recalculatePath(BlockPos pos) {
		if (target != null) {
			// Priority queue to store nodes to be explored, ordered by their cost
			PriorityQueue<PathFinderEntry> queue = new PriorityQueue<>(Comparator.comparing(e -> e.cost));

			// Maps to store parent relationships, visited nodes, and distances
			HashMap<PathNode, PathNode> parentMap = new HashMap<PathNode, PathNode>();
			HashSet<PathNode> visited = new HashSet<PathNode>();
			HashMap<PathNode, Float> distances = new HashMap<PathNode, Float>();

			// Initialize the start node and set its distance to zero
			PathNode startNode = new PathNode(pos);
			distances.put(startNode, 0f);
			queue.add(new PathFinderEntry(startNode, heuristic(startNode, target)));

			PathFinderEntry previousTeleportNode = null;

			// Process nodes in the queue until it is empty
			while (!queue.isEmpty()) {
				PathFinderEntry current = queue.poll();

				// Store the last teleportable block. This is used to continue the parent map
				// from a teleport-able location.
				if (isTeleportable(current.node.pos)) {
					previousTeleportNode = current;
				}

				// Check if the current node is the target
				if (current.node.pos.equals(target)) {
					return reconstructPath(startNode, new PathNode(target), parentMap);
				}

				// Mark the current node as visited
				visited.add(current.node);

				// Explore neighboring nodes
				ArrayList<PathNode> list = getNeighbouringBlocks(current.node);
				for (PathNode node : list) {
					if (visited.contains(node))
						continue;

					float predictedDistanceToTarget = heuristic(node, target);
					PathFinderEntry newEntry = new PathFinderEntry(node, predictedDistanceToTarget);
					queue.add(newEntry);

					if (isTeleportable(node.pos)) {
						// Update distances and parent relationships if a shorter path is found
						if (!distances.containsKey(node) || predictedDistanceToTarget < distances.get(node)) {
							distances.put(node, predictedDistanceToTarget);
							if (previousTeleportNode != null)
								parentMap.put(node, previousTeleportNode.node);
						}
					}
				}
			}
		}

		// Return null if no path is found
		return null;
	}

	/**
	 * Determines if a position can be teleported to.
	 * 
	 * @param pos Position to check.
	 * @return Whether or not the position can be teleported to.
	 */
	private boolean isTeleportable(BlockPos pos) {
		BlockPos down = pos.down();
		BlockState state = MC.world.getBlockState(pos);
		BlockState stateBelow = MC.world.getBlockState(down);

		boolean isPlant = state.getBlock() instanceof PlantBlock;
		boolean isSnow = state.getBlock() instanceof SnowBlock;
		boolean isTorch = state.getBlock() instanceof AbstractTorchBlock;
		boolean isVine = state.getBlock() instanceof VineBlock;

		return isPlayerPassable(pos) && state.canPathfindThrough(NavigationType.LAND)
				&& !state.getFluidState().isIn(FluidTags.WATER) && !state.getFluidState().isIn(FluidTags.LAVA)
				&& !stateBelow.canPathfindThrough(NavigationType.LAND) && !stateBelow.isAir()
				&& stateBelow.getFluidState().isEmpty() && !isPlant && !isSnow && !isTorch && !isVine;
	}

	@Override
	protected ArrayList<PathNode> getNeighbouringBlocks(PathNode node) {
		ArrayList<PathNode> potentialBlocks = new ArrayList<PathNode>(
				List.of(new PathNode(node.pos.north()), new PathNode(node.pos.east()), new PathNode(node.pos.south()),
						new PathNode(node.pos.west()), new PathNode(node.pos.up()), new PathNode(node.pos.down())));
		return potentialBlocks;
	}

	@Override
	protected ArrayList<PathNode> reconstructPath(PathNode start, PathNode target,
			HashMap<PathNode, PathNode> parentMap) {

		float radiusSqr = radius * radius;

		ArrayList<PathNode> path = new ArrayList<>();
		PathNode prev = target;
		path.addFirst(prev);

		// If the start equals the target, return early since there is only one node.
		if (start.equals(target))
			return path;

		// Otherwise traverse the parentMap until we reach the destination.
		// Skips any nodes that are in the same 'direction' as the previous to trim it.
		PathNode current = parentMap.get(prev);
		while (current != null && !current.equals(start)) {
			if (isTeleportable(current.pos)
					&& radiusSqr <= prev.pos.toCenterPos().squaredDistanceTo(current.pos.toCenterPos())) {
				prev = current;
				path.addFirst(current);
			}
			current = parentMap.get(current);
		}
		path.addFirst(start);
		return path;
	}

	@Override
	protected float heuristic(PathNode position, BlockPos target) {
		if (position == null || target == null) {
			throw new IllegalArgumentException("Position and target must not be null");
		}

		// Calculate the squared differences in x, y, and z coordinates
		float dx = (float) Math.abs(position.pos.getX() - target.getX());
		float dy = (float) Math.abs(position.pos.getY() - target.getY());
		float dz = (float) Math.abs(position.pos.getZ() - target.getZ());

		// Return the combined result of the squared differences
		return dx + dy + dz;
	}
}
