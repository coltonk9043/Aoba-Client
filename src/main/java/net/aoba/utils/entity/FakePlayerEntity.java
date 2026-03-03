/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;

public class FakePlayerEntity extends AbstractClientPlayer {

	public FakePlayerEntity() {
		super(Minecraft.getInstance().level, Minecraft.getInstance().player.getGameProfile());
		LocalPlayer player = Minecraft.getInstance().player;

		float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
		setPosRaw(player.position().x, player.position().y, player.position().z);
		setRot(player.getViewYRot(tickDelta), player.getViewXRot(tickDelta));
		// this.inventory = player.getInventory();
	}

	public void despawn() {
		remove(RemovalReason.DISCARDED);
	}
}
