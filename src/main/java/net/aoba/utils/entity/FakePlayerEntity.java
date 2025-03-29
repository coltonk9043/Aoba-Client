/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;

public class FakePlayerEntity extends AbstractClientPlayerEntity {

	public FakePlayerEntity() {
		super(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getGameProfile());
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
		setPos(player.getPos().x, player.getPos().y, player.getPos().z);
		setRotation(player.getYaw(tickDelta), player.getPitch(tickDelta));
		// this.inventory = player.getInventory();
	}

	public void despawn() {
		remove(RemovalReason.DISCARDED);
	}
}
