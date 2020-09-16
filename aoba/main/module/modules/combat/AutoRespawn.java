package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;

public class AutoRespawn extends Module {
	
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
	public void onRender() {
		
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		
	}
	
	@Override
	public void onReceivePacket(IPacket<?> packet) {
		if(packet instanceof SUpdateHealthPacket) {
			SUpdateHealthPacket healthPacket = (SUpdateHealthPacket)packet;
			if (healthPacket.getHealth() > 0.0F)
				return;
			mc.player.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
		}
	}
}
