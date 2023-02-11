package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

public class AutoRespawn extends Module{
	
	public AutoRespawn() {
		this.setName("AutoRespawn");
		this.setBind(new KeyBinding("key.autorespawn", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Automatically respawns when you die.");
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
		
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}
	
	@Override
	public void onReceivePacket(Packet<?> packet) {
		if(packet instanceof HealthUpdateS2CPacket) {
			HealthUpdateS2CPacket healthPacket = (HealthUpdateS2CPacket)packet;
			if (healthPacket.getHealth() > 0.0F)
				return;
			MC.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
		}
	}
}
