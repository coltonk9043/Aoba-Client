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
import java.util.LinkedHashSet;
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
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

public class BlockESP extends Module implements TickListener, Render3DListener, ChunkListener {

	private final BlocksSetting blocks = BlocksSetting.builder().id("blockesp_blocks").displayName("Blocks")
			.description("Blocks that can be seen in Block ESP")
			.defaultValue(new HashSet<Block>(Lists.newArrayList(Blocks.SPAWNER)))
			.onUpdate(s -> onBlocksChanged(s))
			.build();
	
	private final ShaderSetting color = ShaderSetting.builder().id("blockesp_color").displayName("Color")
			.description("Color").defaultValue(Shader.solid(new Color(0f, 1f, 1f, 0.3f))).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("blockesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	private LinkedHashSet<ChunkPos> chunkQueue = new LinkedHashSet<>();
	private HashMap<ChunkPos, ArrayList<BlockPos>> blockPositions = new HashMap<>();
	
	public BlockESP() {
		super("BlockESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see blocks with an ESP.");
		addSetting(blocks);
		addSetting(color);
		addSetting(lineThickness);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(ChunkListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		chunkQueue.clear();
		blockPositions.clear();
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

		blockPositions.forEach((s, v) -> {
			for (BlockPos blockPos : v) {
				AABB box = new AABB(blockPos);
				event.getRenderer().drawBox(box, boxColor, lnThickness);
			}
		});
	}

	@Override
	public void onChunkLoaded(ChunkEvent.Loaded event) {
		chunkQueue.add(event.getChunk().getPos());
	}

	@Override
	public void onChunkUnloaded(ChunkEvent.Unloaded event) {
		ChunkPos pos = event.getChunk().getPos();
		chunkQueue.remove(pos);
		blockPositions.remove(pos);
	}

	@Override
	public void onTick(Pre event) {
		if (!chunkQueue.isEmpty() && MC.level != null) {
			HashSet<Block> blockList = blocks.getValue();

			ChunkPos pos = chunkQueue.getFirst();
			chunkQueue.removeFirst();

			LevelChunk chunk = MC.level.getChunk(pos.x(), pos.z());
			if (chunk == null || chunk.isEmpty())
				return;

			ArrayList<BlockPos> blockPosList = new ArrayList<>();
			chunk.findBlocks(
				(s) -> blockList.contains(s.getBlock()),
				(s, v) -> blockPosList.add(s.immutable())
			);
			if (!blockPosList.isEmpty()) {
				blockPositions.put(pos, blockPosList);
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
		if (MC.level != null) {
			ModuleUtils.getLoadedChunks().forEach(chunk -> chunkQueue.add(chunk.getPos()));
		}
	}
	
}