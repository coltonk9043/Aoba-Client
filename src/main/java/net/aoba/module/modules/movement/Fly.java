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
 * Fly Module
 */
package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;

public class Fly extends Module {

	private SliderSetting flySpeed;
	
	public Fly() {
		this.setName("Fly");
		this.setBind(new KeyBinding("key.fly", GLFW.GLFW_KEY_V, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to fly.");
		
		flySpeed = new SliderSetting("Speed", "fly_speed", 2f, 0.1f, 15f, 0.5f);
		this.addSetting(flySpeed);
	}

	public void setSpeed(float speed) {
		this.flySpeed.setValue(speed);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		ClientPlayerEntity player = MC.player;
		float speed = this.flySpeed.getValueFloat();
		if(MC.player.isRiding()) {
			Entity riding = MC.player.getRootVehicle();
			Vec3d velocity = riding.getVelocity();
			double motionY = MC.options.jumpKey.isPressed() ? 0.3 : 0;
			riding.setVelocity(velocity.x, motionY, velocity.z);
		}else {
			if (MC.options.sprintKey.isPressed()) {
				speed *= 1.5;
			}
			player.setVelocity(new Vec3d(0, 0, 0));
			player.setMovementSpeed(speed * 0.2f);

			Vec3d vec = new Vec3d(0, 0, 0);
			if (MC.options.jumpKey.isPressed()) {
				vec = new Vec3d(0, speed * 0.2f, 0);
			}
			if (MC.options.sneakKey.isPressed()) {
				vec = new Vec3d(0, -speed * 0.2f, 0);
			}
			player.setVelocity(vec);
		}
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
}
