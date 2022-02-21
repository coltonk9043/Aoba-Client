package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;

import net.aoba.gui.Color;
import net.aoba.module.Module;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class PlayerESP extends Module {

	public PlayerESP() {
		this.setName("PlayerESP");
		this.setBind(new KeyBinding("key.playeresp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see other players with an ESP.");

	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {

	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		for (AbstractClientPlayerEntity entity : mc.world.getPlayers()) {
			if(entity != mc.player) {
				this.getRenderUtils().draw3DBox(matrixStack, entity.getBoundingBox(), new Color(255, 0, 0), 0.2f);
			}
		}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}

}
