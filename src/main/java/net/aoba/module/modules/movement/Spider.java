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
 * Spider Module
 */
package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Spider extends Module implements TickListener {

	private FloatSetting speed = FloatSetting.builder().id("spider_speed").displayName("Speed")
			.description("Speed that the player climbs up blocks.").defaultValue(0.1f).minValue(0.05f).maxValue(1f)
			.step(0.05f).build();

	public Spider() {
		super("Spider");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Allows players to climb up blocks like a spider.");
		this.addSetting(speed);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {
		ClientPlayerEntity player = MC.player;

		if (player.horizontalCollision) {
			Vec3d playerVelocity = player.getVelocity();
			MC.player.setVelocity(new Vec3d(playerVelocity.getX(), speed.getValue(), playerVelocity.getZ()));
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
