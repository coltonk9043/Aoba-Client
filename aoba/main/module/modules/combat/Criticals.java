package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CUseEntityPacket;

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
	public void onRender() {

	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		if (packet instanceof CUseEntityPacket) {
			CUseEntityPacket packetUseEntity = (CUseEntityPacket) packet;
			if (packetUseEntity.getAction() == CUseEntityPacket.Action.ATTACK) {
				if(mc.player.isOnGround()) {
					boolean preGround = mc.player.isOnGround();
					mc.player.setOnGround(false);
					mc.player.jump();
					mc.player.setOnGround(preGround);
				}
				
			}
		}
	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {
		
		
	}
}
