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

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Noclip extends Module implements TickListener {
    private FloatSetting flySpeed;
	private FloatSetting speedMultiplier;
	private BooleanSetting onGround;
	private FloatSetting packetDistanceThreshold;
	private FloatSetting packetCountOffset;
	private FloatSetting yawOffset;

	public Noclip() {
        super(new KeybindSetting("key.noclip", "Noclip Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("Noclip");
        this.setCategory(Category.Movement);
        this.setDescription("Allows the player to clip through blocks (Only work clientside).");

        flySpeed = new FloatSetting("noclip_speed", "Speed", "Fly speed.", 2f, 0.1f, 15f, 0.5f);
		speedMultiplier = new FloatSetting("noclip_speedmult", "Speed Multiplier", "Noclip speed multiplier.", 1.5f, 0.1f, 15f, 0.1f);
		onGround = new BooleanSetting("noclip_onground", "On Ground", true);
		packetDistanceThreshold = new FloatSetting("packet_distance_threshold", "Packet Distance Threshold", "Distance threshold for sending packets.", 10f, 1f, 100f, 1f);
		packetCountOffset = new FloatSetting("packet_count_offset", "Packet Count Offset", "Offset for the number of packets required.", 1f, 1f, 10f, 1f);
		yawOffset = new FloatSetting("yaw_offset", "Yaw Offset", "Angle offset for right direction.", 90f, 0f, 360f, 1f);

		this.addSetting(flySpeed);
		this.addSetting(speedMultiplier);
		this.addSetting(onGround);
		this.addSetting(packetDistanceThreshold);
		this.addSetting(packetCountOffset);
		this.addSetting(yawOffset);
	}

    public void setSpeed(float speed) {
        this.flySpeed.setValue(speed);
    }

    public float getSpeed() {
        return this.flySpeed.getValue();
    }

    @Override
    public void onDisable() {
        if (MC.player != null) {
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
            speed *= speedMultiplier.getValue();
        }

        player.setVelocity(new Vec3d(0, 0, 0));

        Vec3d forward = Vec3d.fromPolar(0, player.getYaw());
        Vec3d right = Vec3d.fromPolar(0, player.getYaw() + yawOffset.getValue());

        Vec3d vec = new Vec3d(0, 0, 0);

        if (MC.options.forwardKey.isPressed()) {
            vec = vec.add(forward.multiply(speed));
        } else if (MC.options.backKey.isPressed()) {
            vec = vec.subtract(forward.multiply(speed));
        }

        if (MC.options.rightKey.isPressed()) {
            vec = vec.add(right.multiply(speed));
        } else if (MC.options.leftKey.isPressed()) {
            vec = vec.subtract(right.multiply(speed));
        }


        Vec3d newPos = player.getPos().add(vec);
        int packetsRequired = (int) ((int) Math.ceil(MC.player.getPos().distanceTo(newPos) / packetDistanceThreshold.getValue()) - packetCountOffset.getValue());

        for (int i = 0; i < packetsRequired; i++) {
            MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(onGround.getValue()));
        }

        MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, onGround.getValue()));
    }
}
