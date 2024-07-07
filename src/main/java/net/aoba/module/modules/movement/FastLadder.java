package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class FastLadder extends Module implements TickListener {

    private final FloatSetting ladderSpeed;

    public FastLadder() {
        super(new KeybindSetting("key.fastladder", "FastLadder Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("FastLadder");
        this.setCategory(Category.Movement);
        this.setDescription("Allows players to climb up Ladders faster");

        ladderSpeed = new FloatSetting("fastladder_speed", "Speed", "Speed for FastLadder Module", 0.2f, 0.1f, 0.5f, 0.1f);
        this.addSetting(ladderSpeed);
    }

    @Override
    public void onDisable() {
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

        if (!player.isClimbing() || !player.horizontalCollision)
            return;

        if (player.input.movementForward == 0
                && player.input.movementSideways == 0)
            return;

        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, ladderSpeed.getValue() + 0.08, velocity.z);
    }
}
