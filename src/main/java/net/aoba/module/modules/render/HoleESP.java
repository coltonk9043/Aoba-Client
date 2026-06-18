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

import net.aoba.Aoba;
import net.aoba.event.events.BlockStateEvent;
import net.aoba.event.events.ChunkEvent;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.BlockStateListener;
import net.aoba.event.listeners.ChunkListener;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.utils.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HoleESP extends Module implements TickListener, Render3DListener, ChunkListener, BlockStateListener {

	private final FloatSetting radius = FloatSetting.builder().id("holeesp_radius").displayName("Radius")
			.description("Distance from the player within which holes are drawn.").defaultValue(24f).minValue(1f)
			.maxValue(64f).step(1f).build();

	private final BooleanSetting showUnsafe = BooleanSetting.builder().id("holeesp_show_unsafe")
			.displayName("Show Unsafe")
			.description("Highlight holes that are surrounded by breakable blocks.").defaultValue(true).build();

	private final ShaderSetting safeColor = ShaderSetting.builder().id("holeesp_safe_color").displayName("Safe Color")
			.description("Color for holes surrounded by unbreakable blocks.")
			.defaultValue(Shader.solid(new Color(0f, 1f, 0f, 0.3f))).build();

	private final ShaderSetting unsafeColor = ShaderSetting.builder().id("holeesp_unsafe_color")
			.displayName("Unsafe Color").description("Color for holes surrounded by breakable blocks.")
			.defaultValue(Shader.solid(new Color(1f, 1f, 0f, 0.3f))).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("holeesp_line_thickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines.").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	private final HashSet<ChunkPos> chunkQueue = new HashSet<>();
	private final HashMap<ChunkPos, ArrayList<Hole>> holesByChunk = new HashMap<>();

	private record Hole(AABB box, boolean safe) {
	}

	public HoleESP() {
		super("HoleESP");
		setCategory(Category.of("Render"));
		setDescription("Highlights holes that are safe from crystal damage.");
		addSetting(radius);
		addSetting(showUnsafe);
		addSetting(safeColor);
		addSetting(unsafeColor);
		addSetting(lineThickness);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(ChunkListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(BlockStateListener.class, this);
		chunkQueue.clear();
		holesByChunk.clear();
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.AddListener(ChunkListener.class, this);
		Aoba.getInstance().eventManager.AddListener(BlockStateListener.class, this);

		if (MC.level != null) {
			ModuleUtils.getLoadedChunks().forEach(chunk -> chunkQueue.add(chunk.getPos()));
		}
	}

	@Override
	public void onToggle() {
	}

	@Override
	public void onChunkLoaded(ChunkEvent.Loaded event) {
		chunkQueue.add(event.getChunk().getPos());
	}

	@Override
	public void onChunkUnloaded(ChunkEvent.Unloaded event) {
		ChunkPos pos = event.getChunk().getPos();
		chunkQueue.remove(pos);
		holesByChunk.remove(pos);
	}

	@Override
	public void onBlockStateChanged(BlockStateEvent event) {
		if (MC.level == null)
			return;

		BlockPos bp = event.getBlockPos();
		int cx = bp.getX() >> 4;
		int cz = bp.getZ() >> 4;
		ChunkPos cp = new ChunkPos(cx, cz);
		chunkQueue.add(cp);
		holesByChunk.remove(cp);

		int relX = bp.getX() & 15;
		int relZ = bp.getZ() & 15;

		if (relX == 0 && MC.level.hasChunk(cx - 1, cz)) {
			ChunkPos west = new ChunkPos(cx - 1, cz);
			chunkQueue.add(west);
			holesByChunk.remove(west);
		}
		if (relX == 15 && MC.level.hasChunk(cx + 1, cz)) {
			ChunkPos east = new ChunkPos(cx + 1, cz);
			chunkQueue.add(east);
			holesByChunk.remove(east);
		}
		if (relZ == 0 && MC.level.hasChunk(cx, cz - 1)) {
			ChunkPos north = new ChunkPos(cx, cz - 1);
			chunkQueue.add(north);
			holesByChunk.remove(north);
		}
		if (relZ == 15 && MC.level.hasChunk(cx, cz + 1)) {
			ChunkPos south = new ChunkPos(cx, cz + 1);
			chunkQueue.add(south);
			holesByChunk.remove(south);
		}
	}

	@Override
	public void onTick(Pre event) {
		if (chunkQueue.isEmpty() || MC.level == null || MC.player == null)
			return;

		ChunkPos playerChunkPos = MC.player.chunkPosition();
		ChunkPos target = null;
		int bestDist = Integer.MAX_VALUE;
		for (ChunkPos queued : chunkQueue) {
			int dist = queued.distanceSquared(playerChunkPos);
			if (dist < bestDist) {
				bestDist = dist;
				target = queued;
			}
		}
		chunkQueue.remove(target);

		LevelChunk chunk = MC.level.getChunk(target.x(), target.z());
		if (chunk == null || chunk.isEmpty())
			return;

		ArrayList<Hole> holes = new ArrayList<>();
		int baseX = target.getMinBlockX();
		int baseZ = target.getMinBlockZ();
		int minY = MC.level.getMinY() + 1;
		int maxY = MC.level.getMaxY() - 1;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				for (int y = minY; y <= maxY; y++) {
					pos.set(baseX + dx, y, baseZ + dz);

					if (!MC.level.getBlockState(pos).isAir())
						continue;
					if (!MC.level.getBlockState(pos.above()).isAir())
						continue;

					BlockState below = MC.level.getBlockState(pos.below());
					BlockState north = MC.level.getBlockState(pos.north());
					BlockState south = MC.level.getBlockState(pos.south());
					BlockState east = MC.level.getBlockState(pos.east());
					BlockState west = MC.level.getBlockState(pos.west());

					if (!below.isCollisionShapeFullBlock(MC.level, pos.below())
							|| !north.isCollisionShapeFullBlock(MC.level, pos.north())
							|| !south.isCollisionShapeFullBlock(MC.level, pos.south())
							|| !east.isCollisionShapeFullBlock(MC.level, pos.east())
							|| !west.isCollisionShapeFullBlock(MC.level, pos.west()))
						continue;

					boolean safe = isUnbreakable(below.getBlock()) && isUnbreakable(north.getBlock())
							&& isUnbreakable(south.getBlock()) && isUnbreakable(east.getBlock())
							&& isUnbreakable(west.getBlock());

					holes.add(new Hole(new AABB(pos), safe));
				}
			}
		}

		if (!holes.isEmpty()) {
			holesByChunk.put(target, holes);
		}
	}

	@Override
	public void onTick(Post event) {
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (MC.player == null)
			return;

		double radiusSqr = radius.getValue().doubleValue();
		radiusSqr *= radiusSqr;
		Vec3 playerPos = MC.player.position();
		float thickness = lineThickness.getValue().floatValue();
		boolean includeUnsafe = showUnsafe.getValue();
		Shader safe = safeColor.getValue();
		Shader unsafe = unsafeColor.getValue();

		for (ArrayList<Hole> holes : holesByChunk.values()) {
			for (Hole hole : holes) {
				if (hole.box.getCenter().distanceToSqr(playerPos) > radiusSqr)
					continue;
				if (hole.safe) {
					event.getRenderer().drawBox(hole.box, safe, thickness);
				} else if (includeUnsafe) {
					event.getRenderer().drawBox(hole.box, unsafe, thickness);
				}
			}
		}
	}

	private static boolean isUnbreakable(Block b) {
		return b == Blocks.BEDROCK || b == Blocks.OBSIDIAN || b == Blocks.CRYING_OBSIDIAN
				|| b == Blocks.NETHERITE_BLOCK || b == Blocks.ENDER_CHEST || b == Blocks.RESPAWN_ANCHOR
				|| b == Blocks.ANCIENT_DEBRIS || b == Blocks.ENCHANTING_TABLE;
	}
}
