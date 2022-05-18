package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.StewItem;
import net.minecraft.network.Packet;

public class AutoSoup extends Module {

	public AutoSoup() {
		this.setName("AutoSoup");
		this.setBind(new KeyBinding("key.autosoup", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Automatically consumes soup when health is low (PvP only).");
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
		if(mc.player.getHealth() < 6) {
			int foodSlot= -1;
			for(int i = 0; i< 9; i++) {
				Item item = mc.player.getInventory().getStack(i).getItem();
				
				if(!(item instanceof StewItem)) {
					continue;
				}
			    mc.player.getInventory().selectedSlot = foodSlot;
			    mc.options.useKey.setPressed(true);
			    break;
			}
		}
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
	
	public void setHunger(int hunger) {
		
	}
}