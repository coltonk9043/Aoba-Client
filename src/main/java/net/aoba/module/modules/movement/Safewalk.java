/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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
 * Safewalk Module
 */
package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.core.settings.types.KeybindSetting;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;

public class Safewalk extends Module implements TickListener {

	public Safewalk() {
		super(new KeybindSetting("key.safewalk", "Safewalk Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("Safewalk");
		this.setCategory(Category.Movement);
		this.setDescription("Permanently keeps player in sneaking mode.");
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
	public void OnUpdate(TickEvent event) {
		double x = MC.player.getVelocity().x;
		double y = MC.player.getVelocity().y;
		double z = MC.player.getVelocity().z;
		if (MC.player.isOnGround()) {
			double increment;
			for (increment = 0.05D; x != 0.0D;) {
				if (x < increment && x >= -increment) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= increment;
				} else {
					x += increment;
				}
			}
			for (; z != 0.0D;) {
				if (z < increment && z >= -increment) {
					z = 0.0D;
				} else if (z > 0.0D) {
					z -= increment;
				} else {
					z += increment;
				}
			}
			for (; x != 0.0D && z != 0.0D;) {
				if (x < increment && x >= -increment) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= increment;
				} else {
					x += increment;
				}
				if (z < increment && z >= -increment) {
					z = 0.0D;
				} else if (z > 0.0D) {
					z -= increment;
				} else {
					z += increment;
				}
			}
		}
		MC.player.setVelocity(new Vec3d(x,y,z));
	}
}

