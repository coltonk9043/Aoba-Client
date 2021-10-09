package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Criticals extends Module {

	public Criticals() {
		this.setName("Criticals");
		this.setBind(new KeyBinding("key.criticals", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Makes all attacks into critical strikes.");
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
//		if (packet instanceof UseEntityC2SPacket) {
//			CUseEntityPacket packetUseEntity = (CUseEntityPacket) packet;
//			if (packetUseEntity.getAction() == CUseEntityPacket.Action.ATTACK) {
//				if(mc.player.isOnGround()) {
//					boolean preGround = mc.player.isOnGround();
//					mc.player.setOnGround(false);
//					mc.player.jump();
//					mc.player.setOnGround(preGround);
//				}
//				
//			}
//		}
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}
}
