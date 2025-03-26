/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FakePlayer extends Module {
	public static OtherClientPlayerEntity fakePlayer;

	private final StringSetting playerName = StringSetting.builder().id("fakeplayer_name").displayName("Player Name")
			.description("Name of the fake player.").defaultValue("cvs0").build();

	private final BooleanSetting enableRegen = BooleanSetting.builder().id("fakeplayer_regen_enable")
			.displayName("Enable Regeneration").description("Enable regeneration effect.").defaultValue(true).build();

	private final FloatSetting regenDuration = FloatSetting.builder().id("fakeplayer_regen_duration")
			.displayName("Regeneration Duration").description("Duration of regeneration effect.").defaultValue(9999.0f)
			.minValue(0.0f).maxValue(10000.0f).step(1f).build();

	private final FloatSetting regenAmplifier = FloatSetting.builder().id("fakeplayer_regen_amplifier")
			.displayName("Regeneration Amplifier").description("Amplifier level of regeneration effect.")
			.defaultValue(2.0f).minValue(0.0f).maxValue(10.0f).step(1f).build();

	private final BooleanSetting enableAbsorption = BooleanSetting.builder().id("fakeplayer_absorption_enable")
			.displayName("Enable Absorption").description("Enable absorption effect.").defaultValue(true).build();

	private final FloatSetting absorptionDuration = FloatSetting.builder().id("fakeplayer_absorption_duration")
			.displayName("Absorption Duration").description("Duration of absorption effect.").defaultValue(9999.0f)
			.minValue(0.0f).maxValue(10000.0f).step(1f).build();

	private final FloatSetting absorptionAmplifier = FloatSetting.builder().id("fakeplayer_absorption_amplifier")
			.displayName("Absorption Amplifier").description("Amplifier level of absorption effect.").defaultValue(4.0f)
			.minValue(0.0f).maxValue(10.0f).step(1f).build();

	private final BooleanSetting enableResistance = BooleanSetting.builder().id("fakeplayer_resistance_enable")
			.displayName("Enable Resistance").description("Enable resistance effect.").defaultValue(true).build();

	private final FloatSetting resistanceDuration = FloatSetting.builder().id("fakeplayer_resistance_duration")
			.displayName("Resistance Duration").description("Duration of resistance effect.").defaultValue(9999.0f)
			.minValue(0.0f).maxValue(10000.0f).step(1f).build();

	private final FloatSetting resistanceAmplifier = FloatSetting.builder().id("fakeplayer_resistance_amplifier")
			.displayName("Resistance Amplifier").description("Amplifier level of resistance effect.").defaultValue(1.0f)
			.minValue(0.0f).maxValue(10.0f).step(1f).build();

	public FakePlayer() {
		super("FakePlayer");

		setCategory(Category.of("Misc"));
		setDescription("Creates a fake player entity.");

		addSetting(playerName);
		addSetting(enableRegen);
		addSetting(regenDuration);
		addSetting(regenAmplifier);
		addSetting(enableAbsorption);
		addSetting(absorptionDuration);
		addSetting(absorptionAmplifier);
		addSetting(enableResistance);
		addSetting(resistanceDuration);
		addSetting(resistanceAmplifier);
	}

	@Override
	public void onDisable() {
		if (fakePlayer == null)
			return;
		fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
		fakePlayer.onRemoved();
		fakePlayer = null;
	}

	@Override
	public void onEnable() {
		fakePlayer = new OtherClientPlayerEntity(MC.world,
				new GameProfile(UUID.fromString("66123666-6666-6666-6666-666666666600"), playerName.getValue()));
		fakePlayer.copyPositionAndRotation(MC.player);

		MC.world.addEntity(fakePlayer);

		if (enableRegen.getValue()) {
			fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,
					(int) (regenDuration.getValue() * 20), regenAmplifier.getValue().intValue()));
		}
		if (enableAbsorption.getValue()) {
			fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,
					(int) (absorptionDuration.getValue() * 20), absorptionAmplifier.getValue().intValue()));
		}
		if (enableResistance.getValue()) {
			fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,
					(int) (resistanceDuration.getValue() * 20), resistanceAmplifier.getValue().intValue()));
		}
	}

	@Override
	public void onToggle() {

	}
}
