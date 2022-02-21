package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;

public class Tracer extends Module {

	public Tracer() {
		this.setName("Tracer");
		this.setBind(new KeyBinding("key.tracer", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Points toward other players and entities with a line.");

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
		for (Entity entity : mc.world.getEntities()) {
			if(entity instanceof LivingEntity && (entity != mc.player)) {
				//this.getRenderUtils().drawLine3D(mc.player, entity);
			}
		}
	}
	
	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}
}
