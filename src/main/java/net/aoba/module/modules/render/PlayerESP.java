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
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class PlayerESP extends Module implements Render3DListener {
	private final ColorSetting color_default = ColorSetting.builder().id("playeresp_color_default")
			.displayName("Default Color").description("Default Color").defaultValue(new Color(1f, 1f, 0f)).build();

	private final ColorSetting color_friendly = ColorSetting.builder().id("playeresp_color_friendly")
			.displayName("Friendly Color").description("Friendly Color").defaultValue(new Color(0f, 1f, 0f)).build();

	private final ColorSetting color_enemy = ColorSetting.builder().id("playeresp_color_enemy")
			.displayName("Enemy Color").description("Enemy Color").defaultValue(new Color(1f, 0f, 0f)).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("playeresp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public PlayerESP() {
		super("PlayerESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see other players with an ESP.");

		addSetting(color_default);
		addSetting(color_friendly);
		addSetting(color_enemy);
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
		for (AbstractClientPlayerEntity entity : MC.world.getPlayers()) {
			if (entity != MC.player) {
				Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), entity.getBoundingBox(),
						color_default.getValue(), lineThickness.getValue().floatValue());
			}
		}
	}
}
