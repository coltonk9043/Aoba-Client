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

import net.aoba.Aoba;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.mixin.interfaces.IEntityVelocityUpdateS2CPacket;
import net.aoba.mixin.interfaces.IExplosionS2CPacket;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.lwjgl.glfw.GLFW;

public class AntiKnockback extends Module implements ReceivePacketListener {

    public AntiKnockback() {
    	super(KeybindSetting.builder().id("key.antiknockback").displayName("AntiKnockback Key").defaultValue(InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)).build());

        this.setName("AntiKnockback");
        this.setCategory(Category.of("Combat"));
        this.setDescription("Prevents knockback.");
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
    public void OnReceivePacket(ReceivePacketEvent readPacketEvent) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Packet<?> packet = readPacketEvent.GetPacket();

        if (packet instanceof EntityVelocityUpdateS2CPacket) {
            IEntityVelocityUpdateS2CPacket velocityUpdatePacket = (IEntityVelocityUpdateS2CPacket) packet;
            if (velocityUpdatePacket.getId() == mc.player.getId()) {
                velocityUpdatePacket.setVelocityX(0);
                velocityUpdatePacket.setVelocityY(0);
                velocityUpdatePacket.setVelocityZ(0);
            }
        }

        if (packet instanceof ExplosionS2CPacket) {
            IExplosionS2CPacket explosionPacket = (IExplosionS2CPacket) packet;
            explosionPacket.setVelocityX(0);
            explosionPacket.setVelocityY(0);
            explosionPacket.setVelocityZ(0);
        }
    }
}