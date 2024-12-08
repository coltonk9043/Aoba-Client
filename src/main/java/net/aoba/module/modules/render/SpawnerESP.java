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
 * SpawnerESP Module
 */
package net.aoba.module.modules.render;

import java.util.ArrayList;
import java.util.stream.Collectors;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.aoba.utils.render.Render3D;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.util.math.Box;

public class SpawnerESP extends Module implements Render3DListener {

	private ColorSetting color = ColorSetting.builder().id("spawneresp_color").displayName("Color").description("Color")
			.defaultValue(new Color(0f, 1f, 1f, 0.3f)).build();

	private FloatSetting lineThickness = FloatSetting.builder().id("spawneresp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public SpawnerESP() {
		super("SpawnerESP");
		this.setCategory(Category.of("Render"));
		this.setDescription("Allows the player to see spawners with an ESP.");

		this.addSetting(color);
		this.addSetting(lineThickness);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		ArrayList<BlockEntity> blockEntities = ModuleUtils.getTileEntities()
				.collect(Collectors.toCollection(ArrayList::new));

		for (BlockEntity blockEntity : blockEntities) {
			if (blockEntity instanceof MobSpawnerBlockEntity) {
				Box box = new Box(blockEntity.getPos());
				Render3D.draw3DBox(event.GetMatrix(), box, color.getValue(), lineThickness.getValue().floatValue());
			}
		}
	}
}