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
 * AntiKnockback Module
 */
package net.aoba.module.modules.combat;

import java.util.Optional;

import net.aoba.Aoba;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.mixin.interfaces.IEntityVelocityUpdateS2CPacket;
import net.aoba.mixin.interfaces.IExplosionS2CPacket;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

public class AntiKnockback extends Module implements ReceivePacketListener {

	private FloatSetting horizontal = FloatSetting.builder().id("antiknockback_horizontal").displayName("Horizontal")
			.description("Horizontal Velocity").defaultValue(0f).minValue(0f).maxValue(1f).step(0.01f).build();

	private FloatSetting vertical = FloatSetting.builder().id("antiknockback_vertical").displayName("Vertical")
			.description("Vertical Velocity").defaultValue(0f).minValue(0f).maxValue(1f).step(0.01f).build();

	private BooleanSetting noPush = BooleanSetting.builder().id("antiknockback_vertical").displayName("No Push")
			.description("Prevents being pushed by entites.").defaultValue(true).build();

	public AntiKnockback() {
		super("AntiKnockback");

		this.setCategory(Category.of("Combat"));
		this.setDescription("Prevents knockback.");

		this.addSetting(horizontal);
		this.addSetting(vertical);
		this.addSetting(noPush);
	}

	public boolean getNoPush() {
		return this.noPush.getValue();
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onReceivePacket(ReceivePacketEvent readPacketEvent) {
		MinecraftClient mc = MinecraftClient.getInstance();
		Packet<?> packet = readPacketEvent.GetPacket();

		if (packet instanceof EntityVelocityUpdateS2CPacket velocityUpdatePacket) {
			if (velocityUpdatePacket.getEntityId() == mc.player.getId()) {
				((IEntityVelocityUpdateS2CPacket) packet)
						.setVelocityX((int) (velocityUpdatePacket.getVelocityX() * 8000d * horizontal.getValue()));
				((IEntityVelocityUpdateS2CPacket) packet)
						.setVelocityY((int) (velocityUpdatePacket.getVelocityY() * 8000d * vertical.getValue()));
				((IEntityVelocityUpdateS2CPacket) packet)
						.setVelocityZ((int) (velocityUpdatePacket.getVelocityZ() * 8000d * horizontal.getValue()));
			}
		}

		// Cancel any explosions.
		if (packet instanceof ExplosionS2CPacket explosionS2CPacket) {
			Optional<Vec3d> knockbackOptional = explosionS2CPacket.playerKnockback();
			if (!knockbackOptional.isEmpty()) {
				Vec3d knockback = knockbackOptional.get();
				((IExplosionS2CPacket) packet)
						.setPlayerKnockback(Optional.of(knockback.multiply(horizontal.getValue())));
			}
		}
	}
}