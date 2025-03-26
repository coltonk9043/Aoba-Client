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

import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

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
    public ArrayList<PathNode> recalculatePath(BlockPos pos) {
        if (target != null) {
            if (MC.player == null || MC.player.isDead()) {
                return null;
            }
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

                // Explore neighboring nodes
                ArrayList<PathNode> list = getNeighbouringBlocks(current.node);
                for (PathNode node : list) {
                    if (visited.contains(node))
                        continue;

                    float predictedDistanceToTarget = heuristic(node, target);

                    // Update distances and parent relationships if a shorter path is found
                    if (!distances.containsKey(node) || predictedDistanceToTarget < distances.get(node)) {
                        distances.put(node, predictedDistanceToTarget);
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
        ArrayList<PathNode> result = new ArrayList<>();

        BlockPos bottom = node.pos.down();

        boolean canPassBottom = !node.getWasJump() && isPlayerPassable(bottom) && !node.getIsInWater();
        boolean needsToJump = false;

        if (canPassBottom) {
        	PathNode bottomNode = new PathNode(bottom);
        	
            if(avoidWater && bottomNode.getIsInWater())
            	return new ArrayList<PathNode>();
            
            boolean isInLava = MC.world.getFluidState(bottom).isIn(FluidTags.LAVA);
            if(avoidLava && isInLava)
            	return new ArrayList<PathNode>();
            
            result.add(bottomNode);
        } else {
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

                if (!MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z))
                    continue;

                PathNode newNode = new PathNode(currentBlock); 
                
                if(avoidWater && newNode.getIsInWater())
                	continue;
               
                if(avoidLava && newNode.getIsInLava())
                	continue;
                
                if (isPlayerPassable(currentBlock))    	
                    result.add(newNode);
                else {
                    // Check to see if the player can jump
                    BlockPos above = currentBlock.up();
                    if (isPlayerPassable(above)) {
                        needsToJump = true;
                    }
                }
            }

            for (BlockPos currentBlock : diagonalBlocks) {
                ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(currentBlock.getX()), ChunkSectionPos.getSectionCoord(currentBlock.getZ()));

                if (!MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z))
                    continue;

                PathNode newNode = new PathNode(currentBlock); 
                
                if(avoidWater && newNode.getIsInWater())
                	continue;
                
                if(avoidLava && newNode.getIsInLava())
                	continue;
                
                if (isPlayerPassableDiagonal(node.pos, currentBlock)) {
                    result.add(new PathNode(currentBlock));
                }
            }
        }

        if (needsToJump && !node.getWasJump()) {
            BlockPos top = node.pos.up();
            if (isPlayerPassable(top)) {
                PathNode topNode = new PathNode(top);
                topNode.setWasJump(true);
                result.add(topNode);
            }
        }

        return result;
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

        if(avoidWater && MC.world.isWater(target)) {
        	dy += 65536;
        }
        
        // Return the combined result of the squared differences
        return dx + dy + dz;
	}
}
