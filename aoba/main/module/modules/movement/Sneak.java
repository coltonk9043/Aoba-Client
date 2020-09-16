package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CEntityActionPacket.Action;

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
		player.connection.sendPacket(new CEntityActionPacket(player, Action.RELEASE_SHIFT_KEY));
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
		player.connection.sendPacket(new CEntityActionPacket(player, Action.PRESS_SHIFT_KEY));
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

