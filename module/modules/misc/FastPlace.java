package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;

import net.aoba.interfaces.IMinecraftClient;
import net.aoba.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class FastPlace extends Module {
	IMinecraftClient iMC;
	
	public FastPlace() {
		this.setName("FastPlace");
		this.setBind(new KeyBinding("key.fastplace", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Places blocks exceptionally fast");
		iMC = (IMinecraftClient)MinecraftClient.getInstance();
	}

	@Override
	public void onDisable() {
		iMC.setRightClickDelay(4);
	}

	@Override
	public void onEnable() {
		
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		iMC.setRightClickDelay(0);
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
