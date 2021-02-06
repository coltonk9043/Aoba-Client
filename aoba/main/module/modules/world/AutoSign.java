package aoba.main.module.modules.world;

import org.lwjgl.glfw.GLFW;

import aoba.main.cmd.CommandManager;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class AutoSign extends Module {
	String[] text;

	public AutoSign() {
		this.setName("AutoSign");
		this.setBind(new KeyBinding("key.autosign", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.World);
		this.setDescription("Automatically places sign.");

	}

	public void setText(String[] text) {
		this.text = text;
	}
	
	public String[] getText() {
		return this.text;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		CommandManager.sendChatMessage("Place down a sign to set text!");
		this.text = null;
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

	}
}
