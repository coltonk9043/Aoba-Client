package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class Sneak extends Module {

	public Sneak() {
		this.setName("Sneak");
		this.setBind(new KeyBinding("key.sneakhack", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Makes the player appear like they're sneaking.");
	}

	@Override
	public void onDisable() {
		ClientPlayerEntity player = mc.player;
		player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.RELEASE_SHIFT_KEY));
	}

	@Override
	public void onEnable() {
		
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		ClientPlayerEntity player = mc.player;
		
		mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.PRESS_SHIFT_KEY));
		mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.RELEASE_SHIFT_KEY));
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

