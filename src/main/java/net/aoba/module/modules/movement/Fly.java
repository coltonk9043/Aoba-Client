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
 * Fly Module
 */
package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.PostTickEvent;
import net.aoba.event.listeners.PostTickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module implements PostTickListener {

    private FloatSetting flySpeed;
    private FloatSetting sprintSpeedMultiplier;
    private FloatSetting jumpMotionY;
    private FloatSetting sneakMotionY;

    public Fly() {
        super(new KeybindSetting("key.fly", "Fly Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_V, 0)));

        this.setName("Fly");
        this.setCategory(Category.of("Movement"));
        this.setDescription("Allows the player to fly.");

        flySpeed = new FloatSetting("fly_speed", "Speed", "Fly speed.", 2f, 0.1f, 15f, 0.5f);
        sprintSpeedMultiplier = new FloatSetting("sprint_speed_multiplier", "Sprint Speed Multiplier", "Speed multiplier when sprinting.", 1.5f, 1.0f, 3.0f, 0.1f);
        jumpMotionY = new FloatSetting("jump_motion_y", "Jump Motion Y", "Upward motion when jump key is pressed.", 0.3f, 0.1f, 2.0f, 0.1f);
        sneakMotionY = new FloatSetting("sneak_motion_y", "Sneak Motion Y", "Downward motion when sneak key is pressed.", -0.3f, -2.0f, 0.0f, 0.1f);

        this.addSetting(flySpeed);
        this.addSetting(sprintSpeedMultiplier);
        this.addSetting(jumpMotionY);
        this.addSetting(sneakMotionY);
    }

    public void setSpeed(float speed) {
        this.flySpeed.setValue(speed);
    }

    public double getSpeed() {
        return this.flySpeed.getValue();
    }


    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(PostTickListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(PostTickListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void onPostTick(PostTickEvent event) {
        ClientPlayerEntity player = MC.player;
        float speed = this.flySpeed.getValue().floatValue();
        if (MC.player.isRiding()) {
            Entity riding = MC.player.getRootVehicle();
            Vec3d velocity = riding.getVelocity();
            double motionY = MC.options.jumpKey.isPressed() ? jumpMotionY.getValue() : 0;
            riding.setVelocity(velocity.x, motionY, velocity.z);
        } else {
            float sprintMultiplier = this.sprintSpeedMultiplier.getValue().floatValue();
            if (MC.options.sprintKey.isPressed()) {
                speed *= sprintMultiplier;
            }
            player.getAbilities().flying = false;
            player.setVelocity(new Vec3d(0, 0, 0));

            Vec3d vec = new Vec3d(0, 0, 0);

            if (MC.options.jumpKey.isPressed()) {
                vec = new Vec3d(0, speed, 0);
            }
            if (MC.options.sneakKey.isPressed()) {
                vec = new Vec3d(0, sneakMotionY.getValue(), 0);
            }
            player.setVelocity(vec);
        }
    }
}