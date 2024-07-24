package net.aoba.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import net.minecraft.util.math.BlockPos;

/**
 * The WalkingPathManager class is responsible for managing and calculating
 * walking paths in the game. It extends the AbstractPathManager and provides
 * implementations for path recalculation and heuristic calculation.
 */
public class WalkingPathManager extends AbstractPathManager {

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
        float dx = (float) Math.pow(position.pos.getX() - target.getX(), 2);
        float dy = (float) Math.pow(position.pos.getY() - target.getY(), 2);
        float dz = (float) Math.pow(position.pos.getZ() - target.getZ(), 2);

        // Return the combined result of the squared differences
        return dx + dy + dz;
	}
}
