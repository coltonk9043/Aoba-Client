package net.aoba.module.modules.misc;

import com.mojang.authlib.GameProfile;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class FakePlayer extends Module {
    public static OtherClientPlayerEntity fakePlayer;

    private StringSetting playerName;
    private BooleanSetting enableRegen;
    private FloatSetting regenDuration;
    private FloatSetting regenAmplifier;
    private BooleanSetting enableAbsorption;
    private FloatSetting absorptionDuration;
    private FloatSetting absorptionAmplifier;
    private BooleanSetting enableResistance;
    private FloatSetting resistanceDuration;
    private FloatSetting resistanceAmplifier;

    public FakePlayer() {
        super(new KeybindSetting("key.fakeplayer", "FakePlayer Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
        this.setName("FakePlayer");
        this.setCategory(Category.Misc);
        this.setDescription("Creates a fake player entity.");

        playerName = new StringSetting("fakeplayer_name", "Player Name", "Name of the fake player.", "cvs0");
        enableRegen = new BooleanSetting("fakeplayer_regen_enable", "Enable Regeneration", "Enable regeneration effect.", true);
        regenDuration = new FloatSetting("fakeplayer_regen_duration", "Regeneration Duration", "Duration of regeneration effect.", 9999.0f, 0.0f, 10000.0f, 1.0f);
        regenAmplifier = new FloatSetting("fakeplayer_regen_amplifier", "Regeneration Amplifier", "Amplifier level of regeneration effect.", 2.0f, 0.0f, 10.0f, 1.0f);
        enableAbsorption = new BooleanSetting("fakeplayer_absorption_enable", "Enable Absorption", "Enable absorption effect.", true);
        absorptionDuration = new FloatSetting("fakeplayer_absorption_duration", "Absorption Duration", "Duration of absorption effect.", 9999.0f, 0.0f, 10000.0f, 1.0f);
        absorptionAmplifier = new FloatSetting("fakeplayer_absorption_amplifier", "Absorption Amplifier", "Amplifier level of absorption effect.", 4.0f, 0.0f, 10.0f, 1.0f);
        enableResistance = new BooleanSetting("fakeplayer_resistance_enable", "Enable Resistance", "Enable resistance effect.", true);
        resistanceDuration = new FloatSetting("fakeplayer_resistance_duration", "Resistance Duration", "Duration of resistance effect.", 9999.0f, 0.0f, 10000.0f, 1.0f);
        resistanceAmplifier = new FloatSetting("fakeplayer_resistance_amplifier", "Resistance Amplifier", "Amplifier level of resistance effect.", 1.0f, 0.0f, 10.0f, 1.0f);

        this.addSetting(playerName);
        this.addSetting(enableRegen);
        this.addSetting(regenDuration);
        this.addSetting(regenAmplifier);
        this.addSetting(enableAbsorption);
        this.addSetting(absorptionDuration);
        this.addSetting(absorptionAmplifier);
        this.addSetting(enableResistance);
        this.addSetting(resistanceDuration);
        this.addSetting(resistanceAmplifier);
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
        fakePlayer = new OtherClientPlayerEntity(MC.world, new GameProfile(UUID.fromString("66123666-6666-6666-6666-666666666600"), playerName.getValue()));
        fakePlayer.copyPositionAndRotation(MC.player);

        MC.world.addEntity(fakePlayer);

        if (enableRegen.getValue()) {
            fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, (int) (regenDuration.getValue() * 20), (int) regenAmplifier.getValue().intValue()));
        }
        if (enableAbsorption.getValue()) {
            fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, (int) (absorptionDuration.getValue() * 20), (int) absorptionAmplifier.getValue().intValue()));
        }
        if (enableResistance.getValue()) {
            fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, (int) (resistanceDuration.getValue() * 20), resistanceAmplifier.getValue().intValue()));
        }
    }

    @Override
    public void onToggle() {

    }
}
