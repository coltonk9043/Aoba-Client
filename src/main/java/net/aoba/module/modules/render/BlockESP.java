/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.google.common.collect.Lists;
import net.aoba.Aoba;
import net.aoba.event.events.ChunkEvent;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.ChunkListener;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.module.modules.render.Tracer.TracerMode;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockESP extends Module implements TickListener, Render3DListener, ChunkListener {

	private final BlocksSetting blocks = BlocksSetting.builder().id("blockesp_blocks").displayName("Blocks")
			.description("Blocks that can be seen in Block ESP")
			.defaultValue(new HashSet<Block>(Lists.newArrayList(Blocks.SPAWNER)))
			.onUpdate(s -> onBlocksChanged(s))
			.build();
	
	private final ShaderSetting color = ShaderSetting.builder().id("blockesp_color").displayName("Color")
			.description("Color").defaultValue(Shader.solid(new Color(0f, 1f, 1f, 0.3f))).build();

	private final FloatSetting maxBlocks = FloatSetting.builder().id("blockesp_max_blocks")
			.displayName("Max Blocks").description("The maximum allowed number of drawn blocks").defaultValue(1024f)
			.minValue(8f).maxValue(65536f).step(1.0f).build();
	
	private final BooleanSetting showTracer = BooleanSetting.builder().id("blockesp_show_tracer")
			.displayName("Show Tracers").description("Shows a tracer from the player's crosshair to the block.").defaultValue(true)
			.build();
	
	private final FloatSetting lineThickness = FloatSetting.builder().id("blockesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	private HashSet<ChunkPos> chunkQueue = new HashSet<>();
	private HashMap<ChunkPos, ArrayList<AABB>> blockPositions = new HashMap<>();
	private int totalBlocks = 0;
	
	public BlockESP() {
		super("BlockESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see blocks with an ESP.");
		addSetting(blocks);
		addSetting(color);
		addSetting(maxBlocks);
		addSetting(showTracer);
		addSetting(lineThickness);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(ChunkListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		chunkQueue.clear();
		blockPositions.clear();
		totalBlocks = 0;
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(ChunkListener.class, this);
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);

		
		if (MC.level != null) {
			ModuleUtils.getLoadedChunks().forEach(chunk -> chunkQueue.add(chunk.getPos()));
		}
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		Shader boxColor = color.getValue();
		float lnThickness = lineThickness.getValue().floatValue();

		// Either show tracer or not.
		// Placed outside of forEach for best performance.
		if(showTracer.getValue()) {
			Entity renderEntity = MC.getCameraEntity() == null ? MC.player : MC.getCameraEntity();
			Vec3 rotation = new Vec3(0, 0, 75).xRot(-(float) Math.toRadians(renderEntity.getXRot()))
					.yRot(-(float) Math.toRadians(renderEntity.getYRot())).add(renderEntity.getEyePosition());
			Vec3 start = new Vec3(rotation.x, rotation.y, rotation.z);
			
			blockPositions.forEach((_, v) -> {
				for (AABB box : v) {
					event.getRenderer().drawBox(box, boxColor, lnThickness);
					event.getRenderer().drawLine(start, box.getCenter(), boxColor, lnThickness);
				}
			});
		}else {
			blockPositions.forEach((_, v) -> {
				for (AABB box : v) {
					event.getRenderer().drawBox(box, boxColor, lnThickness);
				}
			});
		}
	}

	@Override
	public void onChunkLoaded(ChunkEvent.Loaded event) {
		chunkQueue.add(event.getChunk().getPos());
	}

	@Override
	public void onChunkUnloaded(ChunkEvent.Unloaded event) {
		ChunkPos pos = event.getChunk().getPos();
		chunkQueue.remove(pos);
		ArrayList<AABB> removed = blockPositions.remove(pos);
		if (removed != null) {
			totalBlocks -= removed.size();
		}
	}

	@Override
	public void onTick(Pre event) {
		// Return if the number of boxes is ABOVE the max allowable.
		int maxAllowableBlocks = maxBlocks.getValue().intValue();
		if (totalBlocks >= maxAllowableBlocks)
			return;

		if (!chunkQueue.isEmpty() && MC.level != null) {
		
			// Find the closest queued chunk.
			ChunkPos playerChunkPos = MC.player.chunkPosition();
			ChunkPos pos = null;
			int bestDist = Integer.MAX_VALUE;
			for (ChunkPos queuedChunk : chunkQueue) {
				int dist = queuedChunk.distanceSquared(playerChunkPos);
				if (dist < bestDist) {
					bestDist = dist;
					pos = queuedChunk;
				}
			}
			chunkQueue.remove(pos);

			// Ensure that is it not empty.
			LevelChunk chunk = MC.level.getChunk(pos.x(), pos.z());
			if (chunk == null || chunk.isEmpty())
				return;

			// Add blocks from the chunks to the list.
			int remaining = maxAllowableBlocks - totalBlocks;
			HashSet<Block> blockList = blocks.getValue();
			ArrayList<AABB> boxes = new ArrayList<>();
			chunk.findBlocks(
				(s) -> blockList.contains(s.getBlock()),
				(s, v) -> {
					if (boxes.size() < remaining) {
						boxes.add(new AABB(s));
					}
				}
			);
			
			// Add to the dictionary if not empty.
			if (!boxes.isEmpty()) {
				blockPositions.put(pos, boxes);
				totalBlocks += boxes.size();
			}
		}
	}

	@Override
	public void onTick(Post event) { }
	
	/**
	 * Clears the cached block positions and queues up the chunks for re-scanning.
	 */
	private void onBlocksChanged(HashSet<Block> newBlocks) {
		chunkQueue.clear();
		blockPositions.clear();
		totalBlocks = 0;
		if (MC.level != null) {
			ModuleUtils.getLoadedChunks().forEach(chunk -> chunkQueue.add(chunk.getPos()));
		}
	}
}