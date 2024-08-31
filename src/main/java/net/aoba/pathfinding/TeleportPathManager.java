package net.aoba.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import net.minecraft.block.BlockState;
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
	
	
	/**
	 * Recalculates the path from the given position to the target position.
	 *
	 * @param pos The starting position for path recalculation.
	 * @return An ArrayList of PathNode representing the recalculated path.
	 */
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

			// Process nodes in the queue until it is empty
			while (!queue.isEmpty()) {
				PathFinderEntry current = queue.poll();

				// Check if the current node is the target
				if (current.node.pos.equals(target)) {
					return reconstructPath(startNode, new PathNode(target), parentMap);
				}

				// Mark the current node as visited
				visited.add(current.node);
				float distanceToStart = heuristic(current.node, startNode.pos);

				// Explore neighboring nodes
				ArrayList<PathNode> list = getNeighbouringBlocks(current.node);
				for (PathNode node : list) {
					if (visited.contains(node))
						continue;
					
					float predictedDistanceToTarget = heuristic(node, target);
					float totalDistance = distanceToStart + predictedDistanceToTarget;
					
					// Update distances and parent relationships if a shorter path is found
					if (!distances.containsKey(node) || totalDistance < distances.get(node)) {
						distances.put(node, totalDistance);
						queue.add(new PathFinderEntry(node, predictedDistanceToTarget));
						parentMap.put(node, current.node);
					}
				}
			}
		}

		// Return null if no path is found
		return null;
	}

	@Override
	protected ArrayList<PathNode> getNeighbouringBlocks(PathNode node) {
		List<PathNode> potentialBlocks = List.of(new PathNode(node.pos.north()), new PathNode(node.pos.east()), new PathNode(node.pos.south()),
				new PathNode(node.pos.west()), new PathNode(node.pos.up()), new PathNode(node.pos.down()));
		ArrayList<PathNode> result = new ArrayList<PathNode>();
		
		for(PathNode curNode : potentialBlocks){
			if(MC.world.isAir(curNode.pos.down()) || MC.world.isWater(curNode.pos))
				continue;
			
			result.add(curNode);
		}
		
		return result;
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
		while (!current.equals(start)) {
			PathNode next = parentMap.get(current);
			
			BlockState state = MC.world.getBlockState(current.pos);
			if(!state.blocksMovement() &&  radiusSqr <= prev.pos.toCenterPos().squaredDistanceTo(current.pos.toCenterPos())) {
				prev = current;
				path.addFirst(current);
			}
			current = next;
		}
		path.addFirst(start);
		return path;
	}

	/**
	 * Calculates the heuristic cost from the given position to the target.
	 *
	 * @param position The current PathNode position.
	 * @param target   The target BlockPos position.
	 * @return The heuristic cost as a float.
	 */
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
