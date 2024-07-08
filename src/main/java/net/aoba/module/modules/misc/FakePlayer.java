package net.aoba.module.modules.misc;

import com.mojang.authlib.GameProfile;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class FakePlayer extends Module {
    public static OtherClientPlayerEntity fakePlayer;

    public FakePlayer() {
        super(new KeybindSetting("key.fakeplayer", "FakePlayer Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
        this.setName("FakePlayer");
        this.setCategory(Category.Misc);
        this.setDescription("Creates a fake player entity.");
    }

    @Override
    public void onDisable() {
        if (fakePlayer == null) return;
        fakePlayer.kill();
        fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
        fakePlayer.onRemoved();
        fakePlayer = null;
    }

    @Override
    public void onEnable() {
        fakePlayer = new OtherClientPlayerEntity(MC.world, new GameProfile(UUID.fromString("66123666-6666-6666-6666-666666666600"), "cvs0"));
        fakePlayer.copyPositionAndRotation(MC.player);

        MC.world.addEntity(fakePlayer);

        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 4));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
    }

    @Override
    public void onToggle() {

    }
}
