/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.Interpolation;
import net.aoba.utils.render.Render3D;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Tracer extends Module implements Render3DListener, TickListener {
	private final ColorSetting color_player = ColorSetting.builder().id("tracer_color_player")
			.displayName("Player Color").description("Player Color").defaultValue(new Color(1f, 1f, 0f)).build();
	private final ColorSetting color_passive = ColorSetting.builder().id("tracer_color_passive")
			.displayName("Passive Color").description("Passive Color").defaultValue(new Color(0f, 1f, 1f)).build();
	private final ColorSetting color_enemies = ColorSetting.builder().id("tracer_color_enemy")
			.displayName("Enemy Color").description("Enemy Color").defaultValue(new Color(0f, 1f, 1f)).build();
	private final ColorSetting color_misc = ColorSetting.builder().id("tracer_color_misc").displayName("Misc. Color")
			.description("Misc. Color").defaultValue(new Color(0f, 1f, 1f)).build();
	private final FloatSetting lines = FloatSetting.builder().id("tracer_lines").displayName("Max Lines")
			.description("The maximum amount of lines that can be rendered at once.").defaultValue(100f).minValue(1f)
			.maxValue(300f).step(1f).build();
	private final EnumSetting<TracerTarget> target = EnumSetting.<TracerTarget>builder().id("tracer_targetmode")
			.displayName("Tracer Target").description("The part of the body the tracer will target.")
			.defaultValue(TracerTarget.Head).build();
	private final EnumSetting<TracerMode> mode = EnumSetting.<TracerMode>builder().id("tracer_mode")
			.displayName("Tracer Mode").description("The tracer mode.").defaultValue(TracerMode.Stem).build();

	List<Entity> sorted = new ArrayList<>();

	public enum TracerMode {
		Stem, Fill
	}

	public enum TracerTarget {
		Head, Body, Feet
	}

	public Tracer() {
		super("Tracer");
		setCategory(Category.of("Render"));
		setDescription("Points toward other players and entities with a line.");

		addSetting(color_player);
		addSetting(color_passive);
		addSetting(color_enemies);
		addSetting(color_misc);
		addSetting(lines);
		addSetting(target);
		addSetting(mode);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		boolean viewBobbing = MC.options.getBobView().getValue();

		MC.options.getBobView().setValue(false);

		Entity renderEntity = MC.getCameraEntity() == null ? MC.player : MC.getCameraEntity();

		int i = 0;

		for (Entity entity : sorted) {
			if (i >= lines.getValue()) {
				return;
			}

			if (entity != null && entity != MC.getCameraEntity()
					&& (MC.getCameraEntity() == null || !entity.equals(MC.getCameraEntity().getRootVehicle()))) {
				Vec3d interpolation = Interpolation.interpolateEntity(entity);
				double x = interpolation.getX();
				double y = interpolation.getY();
				double z = interpolation.getZ();

				Box bb;

				// used in the future for an outline mode.
				if (target.getValue() == TracerTarget.Head) {
					bb = new Box(x - 0.25, y + entity.getHeight() - 0.45, z - 0.25, x + 0.25,
							y + entity.getHeight() + 0.055, z + 0.25);
				} else {
					bb = new Box(x - 0.4, y, z - 0.4, x + 0.4, y + entity.getHeight() + 0.18, z + 0.4);
				}

				float distance = renderEntity.distanceTo(entity);

				float red;

				if (distance >= 60.0f) {
					red = 120.0f;
				} else {
					red = distance + distance;
				}
				Color color;

				Color baseColor = color_player.getValue();

				color = new Color(Math.min((int) red, 255), baseColor.getGreen(), baseColor.getBlue(),
						baseColor.getAlpha());

				Vec3d rotation = new Vec3d(0, 0, 75).rotateX(-(float) Math.toRadians(renderEntity.getPitch()))
						.rotateY(-(float) Math.toRadians(renderEntity.getYaw())).add(renderEntity.getEyePos());

				Vec3d eyePos = renderEntity.getEyePos();

				if (mode.getValue() == TracerMode.Stem) {
					Render3D.drawLine3D(event.GetMatrix(), event.getCamera(), new Vec3d(x, y, z),
							new Vec3d(x, renderEntity.getHeight() + y, z), color);
				}

				Vec3d start = new Vec3d(rotation.x, rotation.y, rotation.z);

				switch (target.getValue()) {
				case Head -> Render3D.drawLine3D(event.GetMatrix(), event.getCamera(), start,
						new Vec3d(x, y + entity.getHeight() - 0.18f, z), color);
				case Body -> Render3D.drawLine3D(event.GetMatrix(), event.getCamera(), start,
						new Vec3d(x, y + entity.getHeight() / 2.0f, z), color);
				case Feet ->
					Render3D.drawLine3D(event.GetMatrix(), event.getCamera(), start, new Vec3d(x, y, z), color);
				}

				if (mode.getValue() == TracerMode.Fill) {
					Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), bb,
							new Color(color.getRed(), color.getGreen(), color.getBlue(), 78), 1.0f);
				}

				i++;
			}
		}

		MC.options.getBobView().setValue(viewBobbing);
	}

	@Override
	public void onTick(TickEvent.Pre event) {
		sorted = Aoba.getInstance().entityManager.getEntities();

		try {
			sorted.sort(Comparator.comparingDouble(entity -> MC.player.squaredDistanceTo(entity)));
		} catch (IllegalStateException ignored) {
			// no way to fix.
		}
	}

	@Override
	public void onTick(TickEvent.Post event) {

	}
}
