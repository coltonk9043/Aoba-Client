/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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

	private final ColorSetting color = ColorSetting.builder().id("itemesp_color").displayName("Color")
			.description("Color").defaultValue(new Color(0, 1f, 1f, 0.3f)).build();

	private final BooleanSetting visibilityToggle = BooleanSetting.builder().id("itemesp_visibility")
			.displayName("Visibility").defaultValue(true).build();

	private final FloatSetting range = FloatSetting.builder().id("itemesp_range").displayName("Range")
			.description("Range that the ESP will be drawn on items.").defaultValue(100f).minValue(10f).maxValue(500f)
			.step(5f).build();

	private final ColorSetting rareItemColor = ColorSetting.builder().id("itemesp_rare_color")
			.displayName("Rare Item Color").description("Rare Item Color").defaultValue(new Color(1f, 0.5f, 0f))
			.build();

	private final BooleanSetting colorRarity = BooleanSetting.builder().id("itemesp_color_rarity")
			.displayName("Color Rarity").defaultValue(true).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("itemesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public ItemESP() {
		super("ItemESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see items with an ESP.");

		addSetting(color);
		addSetting(lineThickness);
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
		for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
			if (entity instanceof ItemEntity) {
				Vec3d itemPos = entity.getPos();
				if (playerPos.distanceTo(itemPos) <= range.getValue()) {
					Color finalColor = colorRarity.getValue() ? getColorBasedOnItemRarity(entity) : color.getValue();
					Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), entity.getBoundingBox(), finalColor,
							lineThickness.getValue().floatValue());
				}
			}
		}
	}

	private Color getColorBasedOnItemRarity(Entity entity) {
		boolean isRare = false;

		if (entity instanceof ItemEntity itemEntity) {
            isRare = itemEntity.getStack().getRarity() == Rarity.RARE;
		}

		return isRare ? rareItemColor.getValue() : color.getValue();
	}
}
