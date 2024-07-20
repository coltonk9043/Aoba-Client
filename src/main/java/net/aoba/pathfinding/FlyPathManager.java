package net.aoba.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public class FlyPathManager extends AbstractPathManager {

	@Override
	public ArrayList<PathNode> recalculatePath(BlockPos pos) {
		if (target == null) {
            return null;
        }

        PriorityQueue<PathFinderEntry> queue = new PriorityQueue<>(Comparator.comparing(e -> e.cost));
        HashMap<PathNode, PathNode> parentMap = new HashMap<>();
        HashSet<PathNode> visited = new HashSet<>();
        HashMap<PathNode, Float> distances = new HashMap<>();

        PathNode startNode = new PathNode(pos);
        distances.put(startNode, 0f);
        queue.add(new PathFinderEntry(startNode, heuristic(startNode, target)));

        while (!queue.isEmpty()) {
            PathFinderEntry current = queue.poll();

            // Check if the target node has been reached
            if (current.node.pos.equals(target)) {
                return reconstructPath(startNode, new PathNode(target), parentMap);
            }

            visited.add(current.node);
            float distanceToStart = heuristic(current.node, startNode.pos);

            for (PathNode neighbor : getNeighbouringBlocks(current.node)) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                float predictedDistanceToTarget = heuristic(neighbor, target);
                float totalDistance = distanceToStart + predictedDistanceToTarget;

                // Update distances and queue if this path is better
                if (!distances.containsKey(neighbor) || totalDistance < distances.get(neighbor)) {
                    distances.put(neighbor, totalDistance);
                    queue.add(new PathFinderEntry(neighbor, predictedDistanceToTarget));
                    parentMap.put(neighbor, current.node);
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

        // Apply a larger weight for upward movement
        if (position.pos.getY() < target.getY()) {
            dy *= 1024f;
        }

        float dz = (float) Math.pow(position.pos.getZ() - target.getZ(), 2);

        // Return the combined heuristic value
        return dx + dy + dz;
	}
	
	@Override
	protected ArrayList<PathNode> getNeighbouringBlocks(PathNode node) {
        ArrayList<PathNode> result = new ArrayList<PathNode>();

        // Check first if the player is in the air. If so, we want to only allow the player to go down
        // unless fly is enabled, in which case we can go any direction.
        BlockPos bottom = node.pos.down();

        boolean canPassBottom = !node.getWasJump() && isPlayerPassable(bottom);
        boolean needsToJump = false;

        if (canPassBottom)
            result.add(new PathNode(bottom));

       
            BlockPos north = node.pos.north();
            BlockPos east = node.pos.east();
            BlockPos south = node.pos.south();
            BlockPos west = node.pos.west();

            BlockPos northEast = north.east();
            BlockPos southEast = south.east();
            BlockPos southWest = south.west();
            BlockPos northWest = north.west();

            List<BlockPos> adjacentBlocks = List.of(north, east, south, west);
            List<BlockPos> diagonalBlocks = List.of(northEast, southEast, southWest, northWest);

            for (BlockPos currentBlock : adjacentBlocks) {
            	ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(currentBlock.getX()), ChunkSectionPos.getSectionCoord(currentBlock.getZ()));
          
            	if(!MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z)) {
            		continue;
            	}
            	
                if (isPlayerPassable(currentBlock))
                    result.add(new PathNode(currentBlock));
                else {
                    // Check to see if the player can jump
                    BlockPos above = currentBlock.up();
                    needsToJump |= isPlayerPassable(above);
                }
            }

            for (BlockPos currentBlock : diagonalBlocks) {
            	ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(currentBlock.getX()), ChunkSectionPos.getSectionCoord(currentBlock.getZ()));
                
            	if(!MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z)) {
            		continue;
            	}
            	
                if (isPlayerPassableDiagonal(node.pos, currentBlock))
                    result.add(new PathNode(currentBlock));
            }
        


        
            BlockPos top = node.pos.up();
            if (isPlayerPassable(top)) {
                PathNode topNode = new PathNode(top);
                topNode.setWasJump(needsToJump);
                result.add(topNode);
            }
        

        return result;
    }
}
