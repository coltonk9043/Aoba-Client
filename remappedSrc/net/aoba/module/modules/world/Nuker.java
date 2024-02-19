/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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
 * Nuker Module
 */
package net.aoba.module.modules.world;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;

import net.aoba.Aoba;
import net.aoba.event.events.RenderEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.RenderListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Nuker extends Module implements RenderListener, TickListener {
	private ColorSetting color = new ColorSetting("nuker_color", "Color", "Color", new Color(0, 1f, 1f));
	private FloatSetting radius = new FloatSetting("nuker_radius", "Radius", "Radius", 5f, 0f, 15f, 1f);
	private BlocksSetting blacklist = new BlocksSetting("nuker_blacklist", "Blacklist", "Blocks that will not be broken by Nuker.", Lists.newArrayList());
	
	public Nuker() {
		super(new KeybindSetting("key.nuker", "Nuker Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("Nuker");
		this.setCategory(Category.World);
		this.setDescription("Destroys blocks around the player.");
		
		this.addSetting(radius);
		this.addSetting(color);
		this.addSetting(blacklist);
	}

	public void setRadius(int radius) {
		this.radius.setValue((double)radius);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(RenderListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(RenderListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {
	}

	@Override
	public void OnUpdate(TickEvent event) {
		int rad = radius.getValue().intValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(MC.player.getBlockX() + x, (int) MC.player.getBlockY() + y,
							(int) MC.player.getBlockZ() + z);
					Block block = MC.world.getBlockState(blockpos).getBlock();
					if (block == Blocks.AIR || blacklist.getValue().contains(block))
						continue;
					
					MC.player.networkHandler.sendPacket(
							new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
					MC.player.networkHandler
							.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
				}
			}
		}
	}

	@Override
	public void OnRender(RenderEvent event) {
		int rad = radius.getValue().intValue();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(MC.player.getBlockX()+ x, MC.player.getBlockY() + y,
							MC.player.getBlockZ()+ z);
					Block block = MC.world.getBlockState(blockpos).getBlock();

					if (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA  || blacklist.getValue().contains(block))
						continue;

					RenderUtils.draw3DBox(event.GetMatrixStack(), new Box(blockpos), color.getValue());
				}
			}
		}
	}
}
