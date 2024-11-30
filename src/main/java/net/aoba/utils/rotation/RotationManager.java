package net.aoba.utils.rotation;

import net.minecraft.client.MinecraftClient;

public class RotationManager {
	private static MinecraftClient MC = MinecraftClient.getInstance();

	public RotationManager() {

	}

	public static double getGCD() {
		double f = MC.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
		return f * f * f * 1.2;
	}
}
