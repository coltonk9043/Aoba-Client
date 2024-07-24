package net.aoba.pathfinding;

import net.minecraft.util.math.BlockPos;

/**
 * The PathNode class represents a node in a pathfinding algorithm. It holds a position
 * in the game world and additional state information about the node, such as whether
 * it was reached by a jump.
 */
public class PathNode {
    // The position of this node in the game world
    public final BlockPos pos;
    // A flag indicating if the node was reached by a jump
    private boolean wasJump = false;

    /**
     * Constructs a PathNode with specified coordinates.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param z The z-coordinate of the node.
     */
    public PathNode(int x, int y, int z) {
        this.pos = new BlockPos(x, y, z);
    }

    /**
     * Constructs a PathNode with a given BlockPos.
     *
     * @param pos The BlockPos representing the node's position.
     */
    public PathNode(BlockPos pos) {
        this.pos = pos;
    }

    /**
     * Gets the state of the wasJump flag.
     *
     * @return True if the node was reached by a jump, false otherwise.
     */
    public boolean getWasJump() {
        return this.wasJump;
    }

    /**
     * Sets the state of the wasJump flag.
     *
     * @param state The new state of the wasJump flag.
     */
    public void setWasJump(boolean state) {
        this.wasJump = state;
    }

    /**
     * Generates a hash code for this PathNode. The hash code is based on the x and z
     * coordinates of the position.
     *
     * @return The hash code for this PathNode.
     */
    @Override
    public int hashCode() {
        int x = pos.getX();
        int z = pos.getZ();
        return x & 0xFF | (x & Short.MAX_VALUE) << 8 | (z & Short.MAX_VALUE) << 24 |
                (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? Integer.MAX_VALUE : 0);
    }

    /**
     * Checks if this PathNode is equal to another object. Two PathNodes are considered
     * equal if they have the same x, y, and z coordinates.
     *
     * @param obj The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof PathNode) {
            PathNode path = (PathNode) obj;
            return path.pos.getX() == this.pos.getX() &&
                    path.pos.getY() == this.pos.getY() &&
                    path.pos.getZ() == this.pos.getZ();
        } else {
            return false;
        }
    }
}