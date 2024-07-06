package net.aoba.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class PathManager {
	private static MinecraftClient MC = MinecraftClient.getInstance();

	private BlockPos target;

	private boolean isFinished = false;

	private boolean allowFly = false;
	private boolean allowWater = false;
	private boolean allowLava = false;
	
	public PathManager() {
		
	}

	public PathManager(BlockPos target) {
		this.target = target;
	}

	public BlockPos getTarget() {
		return this.target;
	}

	public void setTarget(BlockPos target) {
		this.target = target;
		isFinished = false;
	}

	public boolean getFlyAllowed() {
		return allowFly;
	}

	public void setFlyAllowed(boolean value) {
		allowFly = value;
	}

	public ArrayList<PathNode> recalculatePath(BlockPos pos) {
		if(this.allowFly)
			return recalculateFly(pos);
		else
			return recalculateNormal(pos);
	}

	private ArrayList<PathNode> recalculateNormal(BlockPos pos) {
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

			isFinished = true;
		}

		return null;
	}
	
	private ArrayList<PathNode> recalculateFly(BlockPos pos) {
		if (target != null) {
			PriorityQueue<PathFinderEntry> queue = new PriorityQueue<>(Comparator.comparing(e -> e.cost));

			HashMap<PathNode, PathNode> parentMap = new HashMap<PathNode, PathNode>();
			HashSet<PathNode> visited = new HashSet<PathNode>();
			HashMap<PathNode, Float> distances = new HashMap<PathNode, Float>();

			PathNode startNode = new PathNode(pos);

			distances.put(startNode, 0f);
			queue.add(new PathFinderEntry(startNode, heuristicFly(startNode, target)));

			while (!queue.isEmpty()) {
				PathFinderEntry current = queue.poll();

				if (current.node.pos.equals(target)) {
					return reconstructPath(startNode, new PathNode(target), parentMap);
				}
				
				visited.add(current.node);
				float distanceToStart = heuristicFly(current.node, startNode.pos);

				ArrayList<PathNode> list = getNeighbouringBlocks(current.node);
				for (PathNode node : list) {
					if (visited.contains(node))
						continue;

					float predictedDistanceToTarget = heuristicFly(node, target);
					float totalDistance = distanceToStart + predictedDistanceToTarget;

					if (!distances.containsKey(node) || totalDistance < distances.get(node)) {
						distances.put(node, totalDistance);
						queue.add(new PathFinderEntry(node, predictedDistanceToTarget));
						parentMap.put(node, current.node);
					}
				}
			}

			isFinished = true;
		}

		return null;
	}
	
	private ArrayList<PathNode> reconstructPath(PathNode start, PathNode target, HashMap<PathNode, PathNode> parentMap) {
		ArrayList<PathNode> path = new ArrayList<>();
		PathNode current = target;
		while (!current.equals(start)) {
			path.addFirst(current);
			current = parentMap.get(current);
		}
		path.addFirst(start);
		return path;
	}

	private float heuristic(PathNode position, BlockPos target) {
		float dx = (float) Math.pow((position.pos.getX() - target.getX()), 2);
		float dy = (float) Math.pow((position.pos.getY() - target.getY()), 2);
		float dz = (float) Math.pow((position.pos.getZ() - target.getZ()), 2);

		// Return the result all of the positions combined.
		return dx + dy + dz;
	}
	
	private float heuristicFly(PathNode position, BlockPos target) {
		float dx = (float) Math.pow((position.pos.getX() - target.getX()), 2);
		
		float dy = (float) Math.pow((position.pos.getY() - target.getY()), 2);
		if(position.pos.getY() < target.getY()) {
			dy *= 1024f;
		}
			
		
		float dz = (float) Math.pow((position.pos.getZ() - target.getZ()), 2);

		// Return the result all of the positions combined.
		return dx + dy + dz;
	}

	private ArrayList<PathNode> getNeighbouringBlocks(PathNode node) {
		ArrayList<PathNode> result = new ArrayList<PathNode>();
		
		// Check first if the player is in the air. If so, we want to only allow the player to go down
		// unless fly is enabled, in which case we can go any direction.
		BlockPos bottom = node.pos.down();
		
		boolean canPassBottom = !node.getWasJump() && isPlayerPassable(bottom);
		boolean needsToJump = false;
		
		if (canPassBottom) 
			result.add(new PathNode(bottom));
		
		if(!canPassBottom || allowFly) {
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
			
			for(BlockPos currentBlock : adjacentBlocks) {
				if (isPlayerPassable(currentBlock))
					result.add(new PathNode(currentBlock));
				else {
					// Check to see if the player can jump
					BlockPos above = currentBlock.up();
					needsToJump |= isPlayerPassable(above);
				}
			}
			
			for(BlockPos currentBlock : diagonalBlocks) {
				if (isPlayerPassableDiagonal(node.pos, currentBlock))
					result.add(new PathNode(currentBlock));
			}
		}
		
		
		if (allowFly || (needsToJump && !node.getWasJump())) {
			BlockPos top = node.pos.up();
			if (isPlayerPassable(top)) {
				PathNode topNode = new PathNode(top);
				topNode.setWasJump(needsToJump);
				result.add(topNode);
			}
				
		}
		
		return result;
	}

	private static boolean isPlayerPassable(BlockPos pos) {
		return checkBodyAndHeadPos(pos);
	}

	private static boolean isPlayerPassableDiagonal(BlockPos prevPos, BlockPos pos) {
		// East = positive X, West = negative X, North = negative Z, South = positive Z
		int dx = (prevPos.getX() - pos.getX());
		int dz = (prevPos.getZ() - pos.getZ());

		BlockPos pos1 = null;
		BlockPos pos2 = null;

		// Check West and East.
		if (dx < 0)
			pos1 = pos.west();
		else if (dx > 0)
			pos1 = pos.east();

		// Check North and South
		if (dz < 0)
			pos2 = pos.north();
		else if (dz > 0)
			pos2 = pos.south();

		if (pos1 == null || pos2 == null)
			return false;
		else
			return checkBodyAndHeadPos(pos) && checkBodyAndHeadPos(pos1) && checkBodyAndHeadPos(pos2);
	}

	private static boolean checkBodyAndHeadPos(BlockPos feetPos) {
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
