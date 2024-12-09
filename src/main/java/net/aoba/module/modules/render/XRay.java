/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * XRay Module
 */
package net.aoba.module.modules.render;

import java.util.HashSet;

import net.aoba.settings.types.BooleanSetting;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BlocksSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.InputUtil;

public class XRay extends Module {
	public BlocksSetting blocks = BlocksSetting.builder().id("xray_blocks").displayName("Blocks")
			.description("Blocks that can be seen in Xray")
			.defaultValue(new HashSet<Block>(Lists.newArrayList(Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK,
					Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.IRON_ORE,
					Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.COAL_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.REDSTONE_ORE,
					Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.NETHER_QUARTZ_ORE, Blocks.MOSSY_COBBLESTONE,
					Blocks.STONE_BRICKS, Blocks.OAK_PLANKS, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
					Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_COAL_ORE)))
			.onUpdate(this::ReloadRenderer).build();

	public BooleanSetting fluids = BooleanSetting.builder().id("fluids").displayName("Show Fluids")
			.description("Show fluids (water/lava) when using Xray")
			.defaultValue(true)
			.onUpdate(this::ReloadRenderer).build();

	public XRay() {
		super("XRay", InputUtil.fromKeyCode(GLFW.GLFW_KEY_X, 0));
		this.setCategory(Category.of("Render"));
		this.setDescription("Allows the player to see ores.");
		this.addSetting(blocks);
		this.addSetting(fluids);
	}

	@Override
	public void onDisable() {
		MC.worldRenderer.reload();
	}

	@Override
	public void onEnable() {
		MC.worldRenderer.reload();

	}

	@Override
	public void onToggle() {

	}

	public boolean isXRayBlock(Block b) {
		HashSet<Block> blockList = blocks.getValue();
		if (blockList.contains(b)) {
			return true;
		}
		return false;
	}

	public void ReloadRenderer(HashSet<Block> block) {
		if (MC.worldRenderer != null && state.getValue()) {
			MC.worldRenderer.reload();
		}
	}

	public void ReloadRenderer(Boolean fluids) {
		if (MC.worldRenderer != null && state.getValue()) {
			MC.worldRenderer.reload();
		}
	}
}
