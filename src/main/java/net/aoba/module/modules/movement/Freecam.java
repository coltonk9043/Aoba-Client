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
 * Freecam Module
 */
package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.PostTickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.PostTickListener;
import net.aoba.utils.entity.FakePlayerEntity;
import net.aoba.mixin.interfaces.ICamera;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class Freecam extends Module implements PostTickListener, Render3DListener {
    private FloatSetting flySpeed;

    private FakePlayerEntity fakePlayer;
    private Vec3d prevPos;
    private Vec3d pos;

    public Freecam() {
        super(new KeybindSetting("key.freecam", "Freecam Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("Freecam");
        this.setCategory(Category.of("Movement"));
        this.setDescription("Allows the player to clip through blocks (Only work clientside).");
        flySpeed = new FloatSetting("freecam_speed", "Speed", "Speed of the Freecam.", 2f, 0.1f, 15f, 0.5f);
        this.addSetting(flySpeed);
    }

    public void setSpeed(float speed) {
        this.flySpeed.setValue(speed);
    }

    public double getSpeed() {
        return this.flySpeed.getValue();
    }

    @Override
    public void onDisable() {
        if (fakePlayer != null)
            fakePlayer.despawn();

        Aoba.getInstance().eventManager.RemoveListener(PostTickListener.class, this);
        Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(PostTickListener.class, this);
        Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);

        ClientPlayerEntity player = MC.player;
        fakePlayer = new FakePlayerEntity();
        fakePlayer.copyFrom(player);
        fakePlayer.setUuid(UUID.randomUUID());
        fakePlayer.headYaw = player.headYaw;
        fakePlayer.bodyYaw = player.bodyYaw;
        fakePlayer.setPitch(player.getPitch());
        MC.world.addEntity(fakePlayer);

        Camera camera = MC.gameRenderer.getCamera();
        ICamera iCamera = (ICamera) camera;
        iCamera.setFocusedEntity(null);

        Vec3d newPos = MC.player.getPos().add(0, 1.5, 0);
        prevPos = newPos;
        pos = newPos;
        iCamera.setCameraPos(pos);


    }

    @Override
    public void onToggle() {
    }

    @Override
    public void onPostTick(PostTickEvent event) {
        Camera camera = MC.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        prevPos = cameraPos;

        Vec3d forward = Vec3d.fromPolar(0, camera.getYaw());
        Vec3d right = Vec3d.fromPolar(0, camera.getYaw() + 90);

        Vec3d velocity = new Vec3d(0, 0, 0);

        if (MC.options.forwardKey.isPressed()) {
            velocity = velocity.add(forward.multiply(flySpeed.getValue()));
        } else if (MC.options.backKey.isPressed()) {
            velocity = velocity.subtract(forward.multiply(flySpeed.getValue()));
        }

        if (MC.options.rightKey.isPressed()) {
            velocity = velocity.add(right.multiply(flySpeed.getValue()));
        } else if (MC.options.leftKey.isPressed()) {
            velocity = velocity.subtract(right.multiply(flySpeed.getValue()));
        }

        if (MC.options.jumpKey.isPressed()) {
            velocity = velocity.add(0, flySpeed.getValue(), 0);
        } else if (MC.options.sneakKey.isPressed())
            velocity = velocity.add(0, -flySpeed.getValue(), 0);

        pos = cameraPos.add(velocity);

        ClientPlayerEntity player = MC.player;
        fakePlayer.setHeadYaw(player.getHeadYaw());
        fakePlayer.setBodyYaw(player.getBodyYaw());
        fakePlayer.setVelocity(player.getVelocity());
        fakePlayer.setPosition(player.getPos());
        fakePlayer.setUuid(player.getUuid());
    }

    @Override
    public void OnRender(Render3DEvent event) {
        Camera camera = MC.gameRenderer.getCamera();
        ICamera iCamera = (ICamera) camera;

        double tickDelta = event.GetPartialTicks();

        ClientPlayerEntity player = MC.player;
        fakePlayer.setPitch(player.getPitch(event.GetPartialTicks()));


        Vec3d interpolatedPos = new Vec3d(
                MathHelper.lerp(tickDelta, prevPos.x, pos.x),
                MathHelper.lerp(tickDelta, prevPos.y, pos.y),
                MathHelper.lerp(tickDelta, prevPos.z, pos.z)
        );
        iCamera.setCameraPos(interpolatedPos);
    }

    public FakePlayerEntity getFakePlayer() {
        return this.fakePlayer;
    }
}
