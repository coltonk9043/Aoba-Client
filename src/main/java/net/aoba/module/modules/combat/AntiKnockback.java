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
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.lwjgl.glfw.GLFW;

public class AntiKnockback extends Module implements ReceivePacketListener {

    private FloatSetting horizontal;
    private FloatSetting vertical;

    public AntiKnockback() {
        super(new KeybindSetting("key.antiknockback", "AntiKnockback Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("AntiKnockback");
        this.setCategory(Category.of("Combat"));
        this.setDescription("Prevents knockback.");

        horizontal = new FloatSetting("killaura_horizontal", "Horizontal", "Horizontal Velocity", 0f, 0f, 1f, 0.01f);
        vertical = new FloatSetting("killaura_vertical", "Vertical", "Vertical Velocity", 0f, 0f, 1f, 0.01f);

        this.addSetting(horizontal);
        this.addSetting(vertical);
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

        if (packet instanceof EntityVelocityUpdateS2CPacket velocityUpdatePacket) {
            if (velocityUpdatePacket.getEntityId() == mc.player.getId()) {
                ((IEntityVelocityUpdateS2CPacket) packet).setVelocityX((int) (velocityUpdatePacket.getVelocityX() * 8000d * horizontal.getValue()));
                ((IEntityVelocityUpdateS2CPacket) packet).setVelocityY((int) (velocityUpdatePacket.getVelocityY() * 8000d * vertical.getValue()));
                ((IEntityVelocityUpdateS2CPacket) packet).setVelocityZ((int) (velocityUpdatePacket.getVelocityZ() * 8000d * horizontal.getValue()));
            }
        }
        if (packet instanceof ExplosionS2CPacket explosionS2CPacket) {
            ((IExplosionS2CPacket) packet).setVelocityX(explosionS2CPacket.getPlayerVelocityX() * horizontal.getValue());
            ((IExplosionS2CPacket) packet).setVelocityY(explosionS2CPacket.getPlayerVelocityY() * vertical.getValue());
            ((IExplosionS2CPacket) packet).setVelocityZ(explosionS2CPacket.getPlayerVelocityZ() * horizontal.getValue());
        }
    }
}