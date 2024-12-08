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
 * ItemESP Module
 */
package net.aoba.module.modules.render;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;

public class ItemESP extends Module implements Render3DListener {

	private ColorSetting color = ColorSetting.builder().id("itemesp_color").displayName("Color").description("Color")
			.defaultValue(new Color(0, 1f, 1f, 0.3f)).build();

	private BooleanSetting visibilityToggle = BooleanSetting.builder().id("itemesp_visibility")
			.displayName("Visibility").defaultValue(true).build();

	private FloatSetting range = FloatSetting.builder().id("itemesp_range").displayName("Range")
			.description("Range that the ESP will be drawn on items.").defaultValue(100f).minValue(10f).maxValue(500f)
			.step(5f).build();

	private ColorSetting rareItemColor = ColorSetting.builder().id("itemesp_rare_color").displayName("Rare Item Color")
			.description("Rare Item Color").defaultValue(new Color(1f, 0.5f, 0f)).build();

	private BooleanSetting colorRarity = BooleanSetting.builder().id("itemesp_color_rarity").displayName("Color Rarity")
			.defaultValue(true).build();

	private FloatSetting lineThickness = FloatSetting.builder().id("itemesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public ItemESP() {
		super("ItemESP");
		this.setCategory(Category.of("Render"));
		this.setDescription("Allows the player to see items with an ESP.");

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
		if (!visibilityToggle.getValue())
			return;

		Vec3d playerPos = MC.player.getPos();
		for (Entity entity : MC.world.getEntities()) {
			if (entity instanceof ItemEntity) {
				Vec3d itemPos = entity.getPos();
				if (playerPos.distanceTo(itemPos) <= range.getValue()) {
					Color finalColor = colorRarity.getValue() ? getColorBasedOnItemRarity(entity) : color.getValue();
					Render3D.draw3DBox(event.GetMatrix(), entity.getBoundingBox(), finalColor,
							lineThickness.getValue().floatValue());
				}
			}
		}
	}

	private Color getColorBasedOnItemRarity(Entity entity) {
		boolean isRare = false;

		if (entity instanceof ItemEntity) {
			ItemEntity itemEntity = (ItemEntity) entity;
			isRare = itemEntity.getStack().getRarity() == Rarity.RARE;
		}

		return isRare ? rareItemColor.getValue() : color.getValue();
	}
}
