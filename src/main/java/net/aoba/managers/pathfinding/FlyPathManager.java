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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

/**
 * The FlyPathManager class is responsible for managing and calculating
 * flying paths in the game. It extends the AbstractPathManager and provides
 * implementations for path recalculation and heuristic calculation for flying.
 */
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
            for (PathNode neighbor : getNeighbouringBlocks(current.node)) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                float predictedDistanceToTarget = heuristic(neighbor, target);
                float totalDistance = predictedDistanceToTarget;

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

        if(avoidWater && MC.world.isWater(target)) {
        	dy += 200;
        }
        
        float dz = (float) Math.pow(position.pos.getZ() - target.getZ(), 2);

        // Return the combined heuristic value
        return dx + dy + dz;
    }

    @Override
    protected ArrayList<PathNode> getNeighbouringBlocks(PathNode node) {
        ArrayList<PathNode> result = new ArrayList<>();

        // Check if the player is in the air and adjust movement accordingly
        BlockPos bottom = node.pos.down();
        boolean canPassBottom = !node.getWasJump() && isPlayerPassable(bottom);
        boolean needsToJump = false;

        if (canPassBottom) {
            result.add(new PathNode(bottom));
        }

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

        // Check adjacent blocks for passability
        for (BlockPos currentBlock : adjacentBlocks) {
            ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(currentBlock.getX()), ChunkSectionPos.getSectionCoord(currentBlock.getZ()));
            if (!MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z))
                continue;
            
            PathNode nextNode = new PathNode(currentBlock);
            if(avoidWater && nextNode.getIsInWater())
            	continue;
            
            if(avoidLava && nextNode.getIsInLava())
            	continue;
            
            if (isPlayerPassable(currentBlock))
                result.add(nextNode);
            else {
                // Check if the player can jump
                BlockPos above = currentBlock.up();
                needsToJump |= isPlayerPassable(above);
            }
        }

        // Check diagonal blocks for passability
        for (BlockPos currentBlock : diagonalBlocks) {
            ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(currentBlock.getX()), ChunkSectionPos.getSectionCoord(currentBlock.getZ()));
            if (!MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z))
                continue;
            
            PathNode nextNode = new PathNode(currentBlock);
            if(avoidWater && nextNode.getIsInWater())
            	continue;
            
            if(avoidLava && nextNode.getIsInLava())
            	continue;

            if (isPlayerPassableDiagonal(node.pos, currentBlock)) {
                result.add(nextNode);
            }
        }

        // Check if the player can move upward
        BlockPos top = node.pos.up();
        if (isPlayerPassable(top)) {
            PathNode topNode = new PathNode(top);
            topNode.setWasJump(needsToJump);
            result.add(topNode);
        }

        return result;
    }
}