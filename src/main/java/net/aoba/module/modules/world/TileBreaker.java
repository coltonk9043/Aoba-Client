/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.world;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class TileBreaker extends Module implements TickListener, Render3DListener {
	private final ArrayList<Block> blocks = new ArrayList<Block>();

	private final FloatSetting radius = FloatSetting.builder().id("tilebreaker_radius").displayName("Radius")
			.description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

	private final ColorSetting color = ColorSetting.builder().id("tilebreaker_color").displayName("Color")
			.description("Color").defaultValue(new Color(0f, 1f, 1f)).build();

	public TileBreaker() {
		super("TileBreaker");
		setCategory(Category.of("World"));
		setDescription("Destroys blocks that can be instantly broken around the player.");

		loadTileBreakerBlocks();

		addSetting(color);
		addSetting(radius);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu
		);
	}

	public void setRadius(int radius) {
		this.radius.setValue((float) radius);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {
	}

	private void loadTileBreakerBlocks() {
		blocks.add(Blocks.TORCH);
		blocks.add(Blocks.WALL_TORCH);
		blocks.add(Blocks.REDSTONE_TORCH);
		blocks.add(Blocks.REDSTONE_WALL_TORCH);
		blocks.add(Blocks.FERN);
		blocks.add(Blocks.LARGE_FERN);
		blocks.add(Blocks.FLOWER_POT);
		blocks.add(Blocks.POTATOES);
		blocks.add(Blocks.CARROTS);
		blocks.add(Blocks.WHEAT);
		blocks.add(Blocks.BEETROOTS);
		blocks.add(Blocks.SUGAR_CANE);
		blocks.add(Blocks.GRASS_BLOCK);
		blocks.add(Blocks.TALL_GRASS);
		blocks.add(Blocks.SEAGRASS);
		blocks.add(Blocks.TALL_SEAGRASS);
		blocks.add(Blocks.DEAD_BUSH);
		blocks.add(Blocks.DANDELION);
		blocks.add(Blocks.ROSE_BUSH);
		blocks.add(Blocks.POPPY);
		blocks.add(Blocks.BLUE_ORCHID);
		blocks.add(Blocks.ALLIUM);
		blocks.add(Blocks.AZURE_BLUET);
		blocks.add(Blocks.RED_TULIP);
		blocks.add(Blocks.ORANGE_TULIP);
		blocks.add(Blocks.WHITE_TULIP);
		blocks.add(Blocks.PINK_TULIP);
		blocks.add(Blocks.OXEYE_DAISY);
		blocks.add(Blocks.CORNFLOWER);
		blocks.add(Blocks.WITHER_ROSE);
		blocks.add(Blocks.LILY_OF_THE_VALLEY);
		blocks.add(Blocks.BROWN_MUSHROOM);
		blocks.add(Blocks.RED_MUSHROOM);
		blocks.add(Blocks.SUNFLOWER);
		blocks.add(Blocks.LILAC);
		blocks.add(Blocks.PEONY);
	}

	public boolean isTileBreakerBlock(Block b) {
		return blocks.contains(b);
	}

	@Override
	public void onRender(Render3DEvent event) {
		int rad = radius.getValue().intValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(MC.player.getBlockX() + x, MC.player.getBlockY() + y,
							MC.player.getBlockZ() + z);
					Block block = MC.world.getBlockState(blockpos).getBlock();
					if (isTileBreakerBlock(block)) {
						Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), new Box(blockpos), color.getValue(),
								1.0f);
					}
				}
			}
		}
	}

	@Override
	public void onTick(Pre event) {
		int rad = radius.getValue().intValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(MC.player.getBlockX() + x, MC.player.getBlockY() + y,
							MC.player.getBlockZ() + z);
					Block block = MC.world.getBlockState(blockpos).getBlock();
					if (isTileBreakerBlock(block)) {
						MC.player.networkHandler.sendPacket(
								new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
						MC.player.networkHandler.sendPacket(
								new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
					}
				}
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
