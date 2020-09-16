package aoba.main.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;

public class AutoFish extends Module {
	public AutoFish() {
		this.setName("AutoFish");
		this.setBind(new KeyBinding("key.autofish", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Automatically fishes for you.");
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
		if(packet instanceof SPlaySoundEffectPacket) {
			SPlaySoundEffectPacket soundPacket = (SPlaySoundEffectPacket)packet;
			if(soundPacket.getSound() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
				recastRod();
			}
		}
	}
	
	private void recastRod() {
		CPlayerTryUseItemPacket packetTryUse = new CPlayerTryUseItemPacket(Hand.MAIN_HAND);
		mc.player.connection.sendPacket(packetTryUse);
		mc.player.connection.sendPacket(packetTryUse);
	}

}
