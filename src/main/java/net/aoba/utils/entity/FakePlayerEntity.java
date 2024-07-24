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
 * A class to represent a fake player.
 */
package net.aoba.utils.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;

public class FakePlayerEntity extends AbstractClientPlayerEntity {

    public FakePlayerEntity() {
        super(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getGameProfile());
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        this.setPos(player.getPos().x, player.getPos().y, player.getPos().z);
        this.setRotation(player.getYaw(tickDelta), player.getPitch(tickDelta));
        //this.inventory = player.getInventory();
    }

    public void despawn() {
        this.remove(RemovalReason.DISCARDED);
    }
}
