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
package net.aoba.gui.hud;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.misc.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Matrix4f;

public class RadarHud extends AbstractHud {

	float distance = 50;

	public RadarHud(int x, int y, int width, int height) {
		super("RadarHud", x, y, width, height);
		
		this.minHeight = 180.0f;
		this.minWidth = 180.0f;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
		
		if (this.visible) {
			MatrixStack matrixStack = drawContext.getMatrices();
			Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				float x = pos.getX().floatValue();
				float y = pos.getY().floatValue();
				float width = pos.getWidth().floatValue();
				float height = pos.getHeight().floatValue();

				// Draws background depending on components width and height
				Render2D.drawRoundedBox(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(),
						GuiManager.backgroundColor.getValue());
				Render2D.drawRoundedOutline(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(),
						GuiManager.borderColor.getValue());

				// Draw the 'Radar'
				Render2D.drawBox(matrix4f, x, y + (height / 2), width - 1, 1, new Color(128, 128, 128, 255));
				Render2D.drawBox(matrix4f, x + (width / 2), y, 1, height, new Color(128, 128, 128, 255));
				Render2D.drawBox(matrix4f, x + (width / 2) - 2, y + (height / 2) - 2, 5, 5,
						GuiManager.foregroundColor.getValue());

				float sin_theta = (float) Math.sin(Math.toRadians(-mc.player.getRotationClient().y));
				float cos_theta = (float) Math.cos(Math.toRadians(-mc.player.getRotationClient().y));

				float center_x = x + (width / 2);
				float center_y = y - 2 + (height / 2);

				// Render Entities
				for (Entity entity : mc.world.getEntities()) {
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

					float ratio_x = (float) ((entity.getX() - mc.player.getX())) / (distance);
					float ratio_y = (float) ((entity.getZ() - mc.player.getZ())) / (distance);

					float fake_x = (x + (width / 2) - (width * ratio_x / 2));
					float fake_y = (y - 1.5f + (height / 2) - (width * ratio_y / 2));

					float radius_x = (float) ((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y)))
							+ center_x;
					float radius_y = (float) ((sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y)))
							+ center_y;

					Render2D.drawBox(matrix4f, (int) (Math.min(x + width, Math.max(x, radius_x))),
							(int) (Math.min(y + height, Math.max(y, radius_y))), 3, 3, c);
				}

				// Render Players
				for (AbstractClientPlayerEntity entity : mc.world.getPlayers()) {
					if (entity != mc.player) {
						float ratio_x = (float) ((entity.getX() - mc.player.getX())) / (distance);
						float ratio_y = (float) ((entity.getZ() - mc.player.getZ())) / (distance);

						float fake_x = (x + (width / 2) - (width * ratio_x / 2));
						float fake_y = (y - 1.5f + (height / 2) - (width * ratio_y / 2));

						float radius_x = (float) ((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y)))
								+ center_x;
						float radius_y = (float) ((sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y)))
								+ center_y;

						Render2D.drawBox(matrix4f, (int) (Math.min(x + width, Math.max(x, radius_x))),
								(int) (Math.min(y + height, Math.max(y, radius_y))), 3, 3,
								new Color(255, 255, 255, 255));
						Render2D.drawStringWithScale(drawContext, entity.getName().getString(),
								(int) (Math.min(x + width - 5, Math.max(x, radius_x)))
										- (mc.textRenderer.getWidth(entity.getName()) * 0.5f),
								(int) (Math.min(y + 25 + height, Math.max(y, radius_y))) - 10,
								GuiManager.foregroundColor.getValue(), 1.0f);
					}
				}
			}
		}
	}

	@Override
	public void update() {

	}
}