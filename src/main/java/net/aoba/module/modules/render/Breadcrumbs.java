/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.render;

import java.util.LinkedList;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.util.math.Vec3d;

public class Breadcrumbs extends Module implements Render3DListener, TickListener {

	private final ColorSetting color = ColorSetting.builder().id("breadcrumbs_color").displayName("Color")
			.description("Color").defaultValue(new Color(0, 1f, 1f)).build();

	private final float distanceThreshold = 1.0f; // Minimum distance to record a new position
	private float currentTick = 0;
	private final float timer = 10;
	private final LinkedList<Vec3d> positions = new LinkedList<>();
	private final int maxPositions = 1000;

	public Breadcrumbs() {
		super("Breadcrumbs");
		setCategory(Category.of("Render"));
		setDescription("Shows breadcrumbs of where you last stepped;");
		addSetting(color);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		positions.clear();
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
		Vec3d prevPosition = null;
		for (Vec3d position : positions) {
			if (prevPosition != null) {
				Render3D.drawLine3D(event.GetMatrix(), event.getCamera(), prevPosition, position, color.getValue());
			}
			prevPosition = position;
		}
	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		currentTick++;
		if (timer == currentTick) {
			currentTick = 0;
			if (!Aoba.getInstance().moduleManager.freecam.state.getValue()) {
				Vec3d currentPosition = MC.player.getPos();
				if (positions.isEmpty() || positions.getLast().squaredDistanceTo(currentPosition) >= distanceThreshold
						* distanceThreshold) {
					if (positions.size() >= maxPositions) {
						positions.removeFirst();
					}
					positions.add(currentPosition);
				}
			}
		}
	}
}