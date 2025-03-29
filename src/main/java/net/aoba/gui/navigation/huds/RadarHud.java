/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.ResizeMode;
import net.aoba.gui.colors.Color;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

public class RadarHud extends HudWindow {
	float distance = 50;

	public RadarHud(float x, float y) {
		super("RadarHud", x, y, 180f, 180f);

		minHeight = 180.0f;
		minWidth = 180.0f;
		resizeMode = ResizeMode.WidthAndHeight;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = getActualSize();
			if (pos.isDrawable()) {
				float x = pos.getX().floatValue();
				float y = pos.getY().floatValue();
				float width = pos.getWidth().floatValue();
				float height = pos.getHeight().floatValue();

				// Draws background depending on components width and height
				Render2D.drawOutlinedRoundedBox(drawContext, x, y, width, height, GuiManager.roundingRadius.getValue(),
						GuiManager.borderColor.getValue(), GuiManager.backgroundColor.getValue());

				// Draw the 'Radar'
				Render2D.drawBox(drawContext, x, y + (height / 2), width - 1, 1, new Color(128, 128, 128, 255));
				Render2D.drawBox(drawContext, x + (width / 2), y, 1, height, new Color(128, 128, 128, 255));
				Render2D.drawBox(drawContext, x + (width / 2) - 2, y + (height / 2) - 2, 5, 5,
						GuiManager.foregroundColor.getValue());

				float sin_theta = (float) Math.sin(Math.toRadians(-MC.player.getRotationClient().y));
				float cos_theta = (float) Math.cos(Math.toRadians(-MC.player.getRotationClient().y));

				float center_x = x + (width / 2);
				float center_y = y - 2 + (height / 2);

				// Render Entities
				for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
					Color c;
					if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
						if (entity instanceof AnimalEntity) {
							c = new Color(0, 255, 0);
						} else if (entity instanceof Monster) {
							c = new Color(255, 0, 0);
						} else {
							c = new Color(0, 0, 255);
						}
					} else {
						continue;
					}

					float ratio_x = (float) ((entity.getX() - MC.player.getX())) / (distance);
					float ratio_y = (float) ((entity.getZ() - MC.player.getZ())) / (distance);

					float fake_x = (x + (width / 2) - (width * ratio_x / 2));
					float fake_y = (y - 1.5f + (height / 2) - (width * ratio_y / 2));

					float radius_x = ((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y))) + center_x;
					float radius_y = (sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y)) + center_y;

					Render2D.drawBox(drawContext, (int) (Math.min(x + width, Math.max(x, radius_x))),
							(int) (Math.min(y + height, Math.max(y, radius_y))), 3, 3, c);
				}

				// Render Players
				for (AbstractClientPlayerEntity entity : MC.world.getPlayers()) {
					if (entity != MC.player) {
						float ratio_x = (float) ((entity.getX() - MC.player.getX())) / (distance);
						float ratio_y = (float) ((entity.getZ() - MC.player.getZ())) / (distance);

						float fake_x = (x + (width / 2) - (width * ratio_x / 2));
						float fake_y = (y - 1.5f + (height / 2) - (width * ratio_y / 2));

						float radius_x = ((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y)))
								+ center_x;
						float radius_y = (sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y))
								+ center_y;

						Render2D.drawBox(drawContext, (int) (Math.min(x + width, Math.max(x, radius_x))),
								(int) (Math.min(y + height, Math.max(y, radius_y))), 3, 3,
								new Color(255, 255, 255, 255));
						Render2D.drawStringWithScale(drawContext, entity.getName().getString(),
								(int) (Math.min(x + width - 5, Math.max(x, radius_x)))
										- (MC.textRenderer.getWidth(entity.getName()) * 0.5f),
								(int) (Math.min(y + 25 + height, Math.max(y, radius_y))) - 10,
								GuiManager.foregroundColor.getValue(), 1.0f);
					}
				}
			}
		}
		super.draw(drawContext, partialTicks);
	}
}