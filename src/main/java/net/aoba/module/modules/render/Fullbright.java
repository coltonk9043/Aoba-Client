package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;

import net.aoba.interfaces.ISimpleOption;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Fullbright extends Module {

	private double previousValue = 0.0;
	public Fullbright() {
		this.setName("Fullbright");
		this.setBind(new KeyBinding("key.fullbright", GLFW.GLFW_KEY_F, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Maxes out the brightness.");

	}

	@Override
	public void onDisable() {
		@SuppressWarnings("unchecked")
		ISimpleOption<Double> gamma =
				(ISimpleOption<Double>)(Object)MC.options.getGamma();
		gamma.forceSetValue(previousValue);
	}

	@Override
	public void onEnable() {
		this.previousValue = MC.options.getGamma().getValue();
		@SuppressWarnings("unchecked")
		ISimpleOption<Double> gamma =
				(ISimpleOption<Double>)(Object)MC.options.getGamma();
		gamma.forceSetValue(10000.0);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {

	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
	
		
	}

}
