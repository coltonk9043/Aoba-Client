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
 * NoSlowdown Module
 */
package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.mixin.interfaces.IEntity;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.util.math.Vec3d;

public class NoSlowdown extends Module implements TickListener {

	private FloatSetting slowdownMultiplier = FloatSetting.builder().id("noslowdown_multiplier")
			.displayName("Multiplier").description("NoSlowdown walk speed multiplier.").defaultValue(0f).minValue(0f)
			.maxValue(1f).step(0.1f).build();

	public NoSlowdown() {
		super("NoSlowdown");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Prevents the player from being slowed down by blocks.");

		this.addSetting(slowdownMultiplier);
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
		IEntity playerEntity = (IEntity) MC.player;

		if (!playerEntity.getMovementMultiplier().equals(Vec3d.ZERO)) {
			float multiplier = slowdownMultiplier.getValue();
			if (multiplier == 0.0f) {
				playerEntity.setMovementMultiplier(Vec3d.ZERO);
			} else {
				playerEntity.setMovementMultiplier(Vec3d.ZERO.add(1, 1, 1).multiply(1 / multiplier));
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
