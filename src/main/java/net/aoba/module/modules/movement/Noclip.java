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
 * Noclip Module
 */
package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Noclip extends Module implements TickListener {
	private FloatSetting flySpeed;
	
	public Noclip() {
		super(new KeybindSetting("key.noclip", "Noclip Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("Noclip");
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to clip through blocks (Only work clientside).");
		
		flySpeed = new FloatSetting("noclip_speed", "Speed", "Fly speed.", 2f, 0.1f, 15f, 0.5f);
		this.addSetting(flySpeed);
	}

	public void setSpeed(float speed) {
		this.flySpeed.setValue(speed);
	}
	
	public float getSpeed() {
		return this.flySpeed.getValue();
	}
	
	@Override
	public void onDisable() {
		if(MC.player != null) {
			MC.player.noClip = false;
		}
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
		ClientPlayerEntity player = MC.player;
		
		float speed = this.flySpeed.getDefaultValue();
		if (MC.options.sprintKey.isPressed()) {
			speed *= 1.5;
		}
		player.setVelocity(new Vec3d(0,0,0));

		Vec3d forward = Vec3d.fromPolar(0, player.getYaw());
        Vec3d right = Vec3d.fromPolar(0, player.getYaw() + 90);
        
        Vec3d vec = new Vec3d(0, 0, 0);

        if(MC.options.forwardKey.isPressed()) {
        	vec = vec.add(forward.multiply(speed));
        }else if (MC.options.backKey.isPressed()) {
        	vec = vec.subtract(forward.multiply(speed));
        }
        
        if(MC.options.rightKey.isPressed()) {
        	vec = vec.add(right.multiply(speed));
        }else if(MC.options.leftKey.isPressed()) {
        	vec = vec.subtract(right.multiply(speed));
        }
		
		
		 Vec3d newPos = player.getPos().add(vec);
         int packetsRequired = (int) Math.ceil(MC.player.getPos().distanceTo(newPos) / 10) - 1;

         for (int i = 0; i < packetsRequired; i++) {
             MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
         }

         MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, true));
	}
}
