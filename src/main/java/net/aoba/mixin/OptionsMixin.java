package net.aoba.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Codec;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

@Mixin(Options.class)
public class OptionsMixin {
	private static final OptionInstance.SliderableValueSet<Double> FULLBRIGHT_VALUES = new OptionInstance.SliderableValueSet<Double>() {
		@Override
		public Optional<Double> validateValue(Double value) {
			return Optional.of(value);
		}

		@Override
		public double toSliderValue(Double value) {
			return Math.min(1.0, value / 100000.0);
		}

		@Override
		public Double fromSliderValue(double sliderValue) {
			return sliderValue * 100000.0;
		}

		@Override
		public Codec<Double> codec() {
			return Codec.DOUBLE;
		}
	};

	private static final OptionInstance<Double> fullbrightOption = new OptionInstance<Double>(
			"options.gamma",
			OptionInstance.noTooltip(),
			(caption, value) -> Component.literal("Fullbright"),
			FULLBRIGHT_VALUES,
			99999.9,
			value -> {});

	@Inject(at = { @At("HEAD") }, method = {
			"gamma()Lnet/minecraft/client/OptionInstance;" }, cancellable = true)
	private void onGetGamma(CallbackInfoReturnable<OptionInstance<Double>> cir) {
		Minecraft MC = Minecraft.getInstance();
		if (MC.screen instanceof OptionsSubScreen)
			return;

		AobaClient aoba = Aoba.getInstance();
		if (aoba == null)
			return;

		if (aoba.moduleManager.fullbright.state.getValue() || aoba.moduleManager.xray.state.getValue()) {
			cir.setReturnValue(fullbrightOption);
		}
	}
}
