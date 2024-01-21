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
 * Freecam Module
 */
package net.aoba.module.modules.movement;

import java.util.UUID;
import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.misc.FakePlayerEntity;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.util.math.Vec3d;

public class Freecam extends Module implements TickListener {
	private FakePlayerEntity fakePlayer;
	private FloatSetting flySpeed;
	
	public Freecam() {
		super(new KeybindSetting("key.freecam", "Freecam Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("Freecam");
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to clip through blocks (Only work clientside).");
		flySpeed = new FloatSetting("freecam_speed", "Speed", "Speed of the Freecam.", 2f, 0.1f, 15f, 0.5f);
		this.addSetting(flySpeed);
	}

	public void setSpeed(float speed) {
		this.flySpeed.setValue((double)speed);
	}
	
	public double getSpeed() {
		return this.flySpeed.getValue();
	}
	
	@Override
	public void onDisable() {
		if(MC.world == null || fakePlayer == null) return;
		ClientPlayerEntity player = MC.player;
		MC.player.noClip = false;
		player.setVelocity(0, 0, 0);
		player.copyFrom(fakePlayer);
		fakePlayer.despawn();
		MC.world.removeEntity(-3, RemovalReason.DISCARDED);
		
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		ClientPlayerEntity player = MC.player;
		fakePlayer = new FakePlayerEntity();
		fakePlayer.copyFrom(player);
		fakePlayer.setUuid(UUID.randomUUID());
		fakePlayer.headYaw = player.headYaw;
		MC.world.addEntity(fakePlayer);
		
		Aoba.getInstance().moduleManager.fly.setState(false);
		Aoba.getInstance().moduleManager.noclip.setState(false);
		
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	public FakePlayerEntity getFakePlayer() {
		return this.fakePlayer;
	}

	@Override
	public void OnUpdate(TickEvent event) {
		ClientPlayerEntity player = MC.player;
		player.noClip = true;
		player.setOnGround(false);
		float speed = this.flySpeed.getValue().floatValue();
		if (MC.options.sprintKey.isPressed()) {
			speed *= 1.5;
		}
		
		player.getAbilities().flying = false;
		player.setVelocity(new Vec3d(0, 0, 0));
		Vec3d vec = new Vec3d(0,0,0);
		if (MC.options.jumpKey.isPressed()) {
			vec = new Vec3d(0,speed * 0.2f,0);
		}
		if (MC.options.sneakKey.isPressed()) {
			vec = new Vec3d(0,-speed * 0.2f,0);
		}

		player.setVelocity(vec);
	}
}
