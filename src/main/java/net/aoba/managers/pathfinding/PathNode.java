/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.pathfinding;

import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
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
    // A flag indicating that this node is in water.
    private boolean isInWater = false;
    // A flag indicating that this node is in lava.
    private boolean isInLava = false;
    
    /**
     * Constructs a PathNode with specified coordinates.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @param z The z-coordinate of the node.
     */
    public PathNode(int x, int y, int z) {
    	MinecraftClient MC = MinecraftClient.getInstance();
        pos = new BlockPos(x, y, z);
        FluidState fluidState = MC.world.getFluidState(pos);
        isInWater = fluidState.isIn(FluidTags.WATER);
        isInLava = fluidState.isIn(FluidTags.LAVA);
    }

    /**
     * Constructs a PathNode with a given BlockPos.
     *
     * @param pos The BlockPos representing the node's position.
     */
    public PathNode(BlockPos pos) {
    	MinecraftClient MC = MinecraftClient.getInstance();
        this.pos = pos;
        FluidState fluidState = MC.world.getFluidState(pos);
        isInWater = fluidState.isIn(FluidTags.WATER);
        isInLava = fluidState.isIn(FluidTags.LAVA);
    }

    /**
     * Gets the state of the wasJump flag.
     *
     * @return True if the node was reached by a jump, false otherwise.
     */
    public boolean getWasJump() {
        return wasJump;
    }

    /**
     * Sets the state of the wasJump flag.
     *
     * @param state The new state of the wasJump flag.
     */
    public void setWasJump(boolean state) {
        wasJump = state;
    }

    /**
     * Gets the state of the isInwater flag
     * @return True if the node is in water, false otherwise.
     */
    public boolean getIsInWater() {
    	return isInWater;
    }
    
    /**
     * Sets the state of the isInWater flag
     * @param state The new state of the isInWater flag.
     */
    public void setIsInWater(boolean state) {
    	isInWater = state;
    }
    
    /**
     * Gets the state of the isInLava flag
     * @return True if the node is in lava, false otherwise.
     */
    public boolean getIsInLava() {
    	return isInLava;
    }
    
    /**
     * Sets the state of the isInLava flag.
     * @param state The new state of the isInLava flag.
     */
    public void setIsInLava(boolean state) {
    	isInLava = state;
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

        if (obj instanceof PathNode path) {
            return path.pos.getX() == pos.getX() &&
                    path.pos.getY() == pos.getY() &&
                    path.pos.getZ() == pos.getZ();
        } else {
            return false;
        }
    }
}