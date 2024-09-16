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
 * NoFall Module
 */
package net.aoba.module.modules.movement;

import net.aoba.module.Category;
import net.aoba.settings.types.FloatSetting;
import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.PostTickEvent;
import net.aoba.event.listeners.PostTickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;

public class NoFall extends Module implements PostTickListener {

	private FloatSetting fallDistance;

	public NoFall() {
		super(new KeybindSetting("key.nofall", "NoFall Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("No-Fall");
        this.setCategory(Category.of("Movement"));
		this.setDescription("Prevents fall damage.");

		fallDistance = new FloatSetting("nofall_falldistance", "Fall Distance", "No-Fall Distance", 2f, 1f, 20f, 1f);
		this.addSetting(fallDistance);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(PostTickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(PostTickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onPostTick(PostTickEvent event) {
		if(MC.player.fallDistance > fallDistance.getValue()) {
			MC.player.networkHandler.sendPacket(new OnGroundOnly(true));
		}
	}
}
