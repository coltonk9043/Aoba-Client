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

import org.lwjgl.glfw.GLFW;
import net.aoba.misc.FakePlayerEntity;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;

public class Freecam extends Module {
	private FakePlayerEntity fakePlayer;
	private SliderSetting flySpeed;
	
	public Freecam() {
		this.setName("Freecam");
		this.setBind(new KeyBinding("key.freecam", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to clip through blocks (Only work clientside).");
		flySpeed = new SliderSetting("Speed", "freecam_speed", 2f, 0.1f, 15f, 0.5f);
		this.addSetting(flySpeed);
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
	}

	@Override
	public void onEnable() {
		ClientPlayerEntity player = MC.player;
		fakePlayer = new FakePlayerEntity();
		fakePlayer.copyFrom(player);
		fakePlayer.headYaw = player.headYaw;
		MC.world.addEntity(-3, fakePlayer);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		ClientPlayerEntity player = MC.player;
		player.noClip = true;
		player.setOnGround(false);
		float speed = this.flySpeed.getValueFloat();
		if (MC.options.sprintKey.isPressed()) {
			speed *= 1.5;
		}
		player.setVelocity(new Vec3d(0,0,0));
		player.setMovementSpeed(speed * 0.2f);
		player.strideDistance = speed * 0.2f;
		
		Vec3d vec = new Vec3d(0,0,0);
		if (MC.options.jumpKey.isPressed()) {
			vec = new Vec3d(0,speed * 0.2f,0);
		}
		if (MC.options.sneakKey.isPressed()) {
			vec = new Vec3d(0,-speed * 0.2f,0);
		}

		player.setVelocity(vec);
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {

	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
	
	public FakePlayerEntity getFakePlayer() {
		return this.fakePlayer;
	}
}
