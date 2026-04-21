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
import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.ResizeMode;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public class RadarHud extends HudWindow {
	private static final Shader GRAY_SHADER = Shader.solid(Colors.Gray);
	private static final Shader ANIMAL_SHADER = Shader.solid(Colors.Green);
	private static final Shader ENEMY_SHADER = Shader.solid(Colors.Red);
	private static final Shader OTHER_SHADER = Shader.solid(Colors.Blue);
	private static final Shader PLAYER_SHADER = Shader.solid(Colors.White);
	float distance = 50;

	public RadarHud(float x, float y) {
		super("RadarHud", x, y, 180f, 180f);

		setProperty(UIElement.MinHeightProperty, 180.0f);
		setProperty(UIElement.MinWidthProperty, 180.0f);
		resizeMode = ResizeMode.WidthAndHeight;
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		if (isVisible) {
			Rectangle pos = getActualSize();
			float x = pos.x();
			float y = pos.y();
			float width = pos.width();
			float height = pos.height();

			// Draws background depending on components width and height
			renderer.drawOutlinedRoundedBox(x, y, width, height, GuiManager.roundingRadius.getValue(),
					GuiManager.windowBorderColor.getValue(), GuiManager.windowBackgroundColor.getValue());

			// Draw the 'Radar'
			renderer.drawBox(x, y + (height / 2), width - 1, 1, GRAY_SHADER);
			renderer.drawBox(x + (width / 2), y, 1, height, GRAY_SHADER);
			renderer.drawBox(x + (width / 2) - 2, y + (height / 2) - 2, 5, 5, GuiManager.foregroundColor.getValue());

			float sin_theta = (float) Math.sin(Math.toRadians(-MC.player.getRotationVector().y));
			float cos_theta = (float) Math.cos(Math.toRadians(-MC.player.getRotationVector().y));

			float center_x = x + (width / 2);
			float center_y = y - 2 + (height / 2);

			// Render Entities
			for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
				Shader c;
				if (entity instanceof LivingEntity && !(entity instanceof Player)) {
					if (entity instanceof Animal) {
						c = ANIMAL_SHADER;
					} else if (entity instanceof Enemy) {
						c = ENEMY_SHADER;
					} else {
						c = OTHER_SHADER;
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

				renderer.drawBox((int) (Math.min(x + width, Math.max(x, radius_x))),
						(int) (Math.min(y + height, Math.max(y, radius_y))), 3, 3, c);
			}

			// Render Players
			for (AbstractClientPlayer entity : MC.level.players()) {
				if (entity != MC.player) {
					float ratio_x = (float) ((entity.getX() - MC.player.getX())) / (distance);
					float ratio_y = (float) ((entity.getZ() - MC.player.getZ())) / (distance);

					float fake_x = (x + (width / 2) - (width * ratio_x / 2));
					float fake_y = (y - 1.5f + (height / 2) - (width * ratio_y / 2));

					float radius_x = ((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y))) + center_x;
					float radius_y = (sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y)) + center_y;

					renderer.drawBox((int) (Math.min(x + width, Math.max(x, radius_x))),
							(int) (Math.min(y + height, Math.max(y, radius_y))), 3, 3, PLAYER_SHADER);
					Font font = GuiManager.fontSetting.getValue().getRenderer();
					renderer.drawStringWithScale(entity.getName().getString(),
							(int) (Math.min(x + width - 5, Math.max(x, radius_x)))
									- (font.width(entity.getName().getString()) * 0.5f),
							(int) (Math.min(y + 25 + height, Math.max(y, radius_y))) - 10,
							GuiManager.foregroundColor.getValue(), 1.0f, font);
				}
			}
		}
		super.draw(renderer, partialTicks);
	}
}