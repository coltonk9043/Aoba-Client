package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.gui.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

public class RadarHud extends AbstractHud {

	float distance = 50;
	public RadarHud( int x, int y, int width, int height) {
		super("RadarHud", x, y, width, height);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		// Draws background depending on components width and height
		renderUtils.drawRoundedBox(matrixStack, x, y, width, height, 6, new Color(30,30,30), 0.4f);
		renderUtils.drawRoundedOutline(matrixStack, x, y, width, height, 6, new Color(0,0,0), 0.8f);

		// Draw the 'Radar'
		renderUtils.drawBox(matrixStack, x , y + (height / 2), width - 1, 1, new Color(128,128,128), 1.0f);
		renderUtils.drawBox(matrixStack, x + (width / 2), y, 1, height, new Color(128,128,128), 1.0f);
		renderUtils.drawBox(matrixStack, x + (width / 2) - 2, y + (height / 2) - 2, 5, 5, Aoba.getInstance().hudManager.getColor(), 1.0f);

		float sin_theta = (float) Math.sin(Math.toRadians(-mc.player.getRotationClient().y));
		float cos_theta = (float) Math.cos(Math.toRadians(-mc.player.getRotationClient().y));

		float center_x = x + (width / 2);
		float center_y = y - 2 + (height / 2);

		// Render Entities
		for (Entity entity : mc.world.getEntities()) {
			Color c ;
			if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
				if (entity instanceof AnimalEntity) {
					c = new Color(0, 255, 0);
				} else if (entity instanceof Monster) {
					c = new Color(255, 0, 0);
				} else {
					c = new Color(0, 0, 255);
				}
			}else {
				continue;
			}

			float ratio_x = (float)((entity.getX() - mc.player.getX())) / (distance);
			float ratio_y = (float)((entity.getZ() - mc.player.getZ())) / (distance);

			float fake_x = (x + (width / 2) - (width * ratio_x / 2));
			float fake_y = (y - 2 + (height / 2) - (width * ratio_y / 2));

			float radius_x = (float)((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y))) + center_x;
			float radius_y = (float)((sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y))) + center_y;

			renderUtils.drawBox(matrixStack, (int)(Math.min(x + width - 5, Math.max(x, radius_x))) , (int)(Math.min(y - 5 + height, Math.max(y, radius_y))), 3, 3, c, 1.0f);
		}

		// Render Players
		for (AbstractClientPlayerEntity entity : mc.world.getPlayers()) {
			if(entity != mc.player) {
				float ratio_x = (float)((entity.getX() - mc.player.getX())) / (distance);
				float ratio_y = (float)((entity.getZ() - mc.player.getZ())) / (distance);

				float fake_x = (x + (width / 2) - (width * ratio_x / 2));
				float fake_y = (y + 28 + (height / 2) - (width * ratio_y / 2));

				float radius_x = (float)((cos_theta * (fake_x - center_x)) - (sin_theta * (fake_y - center_y))) + center_x;
				float radius_y = (float)((sin_theta * (fake_x - center_x)) + (cos_theta * (fake_y - center_y))) + center_y;

				renderUtils.drawBox(matrixStack, (int)(Math.min(x + width - 5, Math.max(x, radius_x))), (int)(Math.min(y + 25 + height, Math.max(y, radius_y))), 3, 3, new Color(255, 255, 255), 1.0f);
				renderUtils.drawStringWithScale(drawContext, entity.getName().getString(), (int)(Math.min(x + width - 5, Math.max(x, radius_x))) - (mc.textRenderer.getWidth(entity.getName()) * 0.5f), (int)(Math.min(y + 25 + height, Math.max(y, radius_y))) - 10, color, 1.0f);
			}
		}
	}
}