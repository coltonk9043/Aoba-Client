package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

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
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}
	
	@Override
	public void onReceivePacket(Packet<?> packet) {
		if(packet instanceof PlaySoundS2CPacket) {
			PlaySoundS2CPacket soundPacket = (PlaySoundS2CPacket)packet;
			if(soundPacket.getSound() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
				recastRod();
			}
		}
	}
	
	private void recastRod() {
		PlayerInteractItemC2SPacket packetTryUse = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND);
		mc.player.networkHandler.sendPacket(packetTryUse);
		mc.player.networkHandler.sendPacket(packetTryUse);
	}

}
