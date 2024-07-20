package net.aoba.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import net.minecraft.util.math.BlockPos;

public class WalkingPathManager extends AbstractPathManager {

	@Override
	public ArrayList<PathNode> recalculatePath(BlockPos pos) {
		if (target != null) {
            PriorityQueue<PathFinderEntry> queue = new PriorityQueue<>(Comparator.comparing(e -> e.cost));

            HashMap<PathNode, PathNode> parentMap = new HashMap<PathNode, PathNode>();
            HashSet<PathNode> visited = new HashSet<PathNode>();
            HashMap<PathNode, Float> distances = new HashMap<PathNode, Float>();

            PathNode startNode = new PathNode(pos);

            distances.put(startNode, 0f);
            queue.add(new PathFinderEntry(startNode, heuristic(startNode, target)));

            while (!queue.isEmpty()) {
                PathFinderEntry current = queue.poll();

                if (current.node.pos.equals(target)) {
                    return reconstructPath(startNode, new PathNode(target), parentMap);
                }

                visited.add(current.node);
                float distanceToStart = heuristic(current.node, startNode.pos);

                ArrayList<PathNode> list = getNeighbouringBlocks(current.node);
                for (PathNode node : list) {
                    if (visited.contains(node))
                        continue;

                    float predictedDistanceToTarget = heuristic(node, target);
                    float totalDistance = distanceToStart + predictedDistanceToTarget;

                    if (!distances.containsKey(node) || totalDistance < distances.get(node)) {
                        distances.put(node, totalDistance);
                        queue.add(new PathFinderEntry(node, predictedDistanceToTarget));
                        parentMap.put(node, current.node);
                    }

                }
            }
        }

        return null;
	}

	@Override
	protected float heuristic(PathNode position, BlockPos target) {
		if (position == null || target == null) {
            throw new IllegalArgumentException("Position and target must not be null");
        }

        float dx = (float) Math.pow(position.pos.getX() - target.getX(), 2);
        float dy = (float) Math.pow(position.pos.getY() - target.getY(), 2);
        float dz = (float) Math.pow(position.pos.getZ() - target.getZ(), 2);

        // Return the combined result of the squared differences
        return dx + dy + dz;
	}
}
