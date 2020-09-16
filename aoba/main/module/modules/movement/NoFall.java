package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;;

public class NoFall extends Module {

	public NoFall() {
		this.setName("No-Fall");
		this.setBind(new KeyBinding("key.nofall", GLFW.GLFW_KEY_HOME, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Prevents fall damage.");
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
		if(mc.player.fallDistance > 2f) {
			mc.player.connection.sendPacket(new CPlayerPacket(true));
		}
	}

	@Override
	public void onRender() {
		
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		
	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {
		
		
	}
}
