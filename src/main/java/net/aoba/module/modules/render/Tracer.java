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
import net.aoba.utils.render.Render2D;
import net.aoba.utils.render.Render3D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Tracer extends Module implements Render3DListener {
	private ColorSetting color_player = ColorSetting.builder().id("tracer_color_player").displayName("Player Color")
			.description("Player Color").defaultValue(new Color(1f, 1f, 0f)).build();

	private ColorSetting color_passive = ColorSetting.builder().id("tracer_color_passive").displayName("Passive Color")
			.description("Passive Color").defaultValue(new Color(0f, 1f, 1f)).build();

	private ColorSetting color_enemies = ColorSetting.builder().id("tracer_color_enemy").displayName("Enemy Color")
			.description("Enemy Color").defaultValue(new Color(0f, 1f, 1f)).build();

	private ColorSetting color_misc = ColorSetting.builder().id("tracer_color_misc").displayName("Misc. Color")
			.description("Misc. Color").defaultValue(new Color(0f, 1f, 1f)).build();

	private FloatSetting lineWidth = FloatSetting.builder().id("tracer_line_width").displayName("Line Width")
			.description("Width of the tracer lines.").defaultValue(1f).minValue(0.1f).maxValue(10f).step(0.1f).build();

	public Tracer() {
		super("Tracer");
		this.setCategory(Category.of("Render"));
		this.setDescription("Points toward other players and entities with a line.");

		this.addSetting(color_player);
		this.addSetting(color_passive);
		this.addSetting(color_enemies);
		this.addSetting(color_misc);
		this.addSetting(lineWidth);
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
		if (Render2D.center == null) {
			return;
		}

		float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
		for (Entity entity : MC.world.getEntities()) {
			if (entity instanceof LivingEntity && (entity != MC.player)) {
				Vec3d interpolated = Render3D.getEntityPositionInterpolated(entity, tickDelta);
				if (entity instanceof AnimalEntity) {
					Render3D.drawLine3D(event.GetMatrix(), Render2D.center, interpolated, color_passive.getValue(),
							lineWidth.getValue());
				} else if (entity instanceof Monster) {
					Render3D.drawLine3D(event.GetMatrix(), Render2D.center, interpolated, color_enemies.getValue(),
							lineWidth.getValue());
				} else {
					Render3D.drawLine3D(event.GetMatrix(), Render2D.center, interpolated, color_misc.getValue(),
							lineWidth.getValue());
				}
			}
		}

		for (AbstractClientPlayerEntity player : MC.world.getPlayers()) {
			if (player == MC.player) continue;
			Vec3d interpolated = Render3D.getEntityPositionInterpolated(player, tickDelta);
			Render3D.drawLine3D(event.GetMatrix(), Render2D.center, interpolated, color_player.getValue(), lineWidth.getValue());
		}
	}
}
