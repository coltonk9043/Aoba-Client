package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.SoupItem;
import net.minecraft.network.IPacket;

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
				Item item = mc.player.inventory.getStackInSlot(i).getItem();
				
				if(!(item instanceof SoupItem)) {
					continue;
				}
			    mc.player.inventory.currentItem = foodSlot;
			    mc.gameSettings.keyBindUseItem.setPressed(true);
			    break;
			}
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
	
	public void setHunger(int hunger) {
		
	}
}