package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Reach extends Module {
	
	private SliderSetting distance;
	
	public Reach() {
		this.setName("Reach");
		this.setBind(new KeyBinding("key.reach", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Reaches further.");
		
		distance = new SliderSetting("Distance", "reach_distance", 5f, 1f, 15f, 1f);
		this.addSetting(distance);
	}

	public float getReach() {
		return distance.getValueFloat();
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
		
		
	}
	
	public void setReachLength(float reach) {
		this.distance.setValue(reach);
	}
}