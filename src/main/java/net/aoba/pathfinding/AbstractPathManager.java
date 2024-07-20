package net.aoba.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public abstract class AbstractPathManager {
	protected static MinecraftClient MC = MinecraftClient.getInstance();
    protected BlockPos target;

    protected boolean allowWater = false;
    protected boolean allowLava = false;

    public AbstractPathManager() {

    }

    public AbstractPathManager(BlockPos target) {
        this.target = target;
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public void setTarget(BlockPos target) {
        this.target = target;
    }

    public abstract ArrayList<PathNode> recalculatePath(BlockPos pos);
    
    protected abstract float heuristic(PathNode position, BlockPos target);

    protected ArrayList<PathNode> reconstructPath(PathNode start, PathNode target, HashMap<PathNode, PathNode> parentMap) {
        ArrayList<PathNode> path = new ArrayList<>();
        PathNode prev = target;
        path.addFirst(prev);
        
        // If the start equals the target, return early since there is only one node.
        if(start.equals(target))
        	return path;
        
        // Otherwise traverse the parentMap until we reach the destination.
        // Skips any nodes that are in the same 'direction' as the previous to trim it.
        PathNode current = parentMap.get(prev);
        BlockPos delta = prev.pos.subtract(current.pos);
        while (!current.equals(start)) {
        	PathNode next = parentMap.get(current);
        	if(!current.pos.subtract(next.pos).equals(delta))
        		path.addFirst(current);
        	prev = current;
            current = next;
            delta = prev.pos.subtract(current.pos);
        }
        path.addFirst(start);
        return path;
    }


    protected ArrayList<PathNode> getNeighbouringBlocks(PathNode node) {
        ArrayList<PathNode> result = new ArrayList<PathNode>();

        // Check first if the player is in the air. If so, we want to only allow the player to go down
        // unless fly is enabled, in which case we can go any direction.
        BlockPos bottom = node.pos.down();

        boolean canPassBottom = !node.getWasJump() && isPlayerPassable(bottom);
        boolean needsToJump = false;

        if (canPassBottom)
            result.add(new PathNode(bottom));
        else {
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
        }


        if (needsToJump && !node.getWasJump()) {
            BlockPos top = node.pos.up();
            if (isPlayerPassable(top)) {
                PathNode topNode = new PathNode(top);
                topNode.setWasJump(needsToJump);
                result.add(topNode);
            }
        }

        return result;
    }

    protected static boolean isPlayerPassable(BlockPos pos) {
        return checkBodyAndHeadPos(pos);
    }

    protected static boolean isPlayerPassableDiagonal(BlockPos prevPos, BlockPos pos) {
        // Calculate the difference in coordinates
        int dx = prevPos.getX() - pos.getX();
        int dz = prevPos.getZ() - pos.getZ();

        // Determine neighboring positions
        BlockPos adjacentPosX = (dx < 0) ? pos.west() : (dx > 0) ? pos.east() : null;
        BlockPos adjacentPosZ = (dz < 0) ? pos.north() : (dz > 0) ? pos.south() : null;

        // Check if both adjacent positions are valid and if the current and adjacent positions are passable
        return adjacentPosX != null && adjacentPosZ != null &&
                checkBodyAndHeadPos(pos) &&
                checkBodyAndHeadPos(adjacentPosX) &&
                checkBodyAndHeadPos(adjacentPosZ);
    }


    protected static boolean checkBodyAndHeadPos(BlockPos feetPos) {
        BlockPos headPos = feetPos.up();
        BlockState state = MC.world.getBlockState(feetPos);
        BlockState stateUp = MC.world.getBlockState(headPos);

        // So... it's depreciated but references isSolid, which in itself is depreciated
        // but is used everywhere in BlockState with no replacement? Makes sense.
        return !state.blocksMovement() && !stateUp.blocksMovement();
    }
}

class PathFinderEntry {
    public PathNode node;
    public float cost;

    public PathFinderEntry(PathNode node, float cost) {
        this.node = node;
        this.cost = cost;
    }
}
