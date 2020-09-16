package aoba.main.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;

public class AutoEat extends Module {

	private int hunger = 6;
	public AutoEat() {
		this.setName("AutoEat");
		this.setBind(new KeyBinding("key.autoeat", GLFW.GLFW_KEY_N, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Automatically eats the best food in your inventory.");
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
		if(mc.player.getFoodStats().getFoodLevel() <= hunger) {
			int foodSlot= -1;
			Food bestFood = null;
			for(int i = 0; i< 9; i++) {
				Item item = mc.player.inventory.getStackInSlot(i).getItem();
				
				if(!item.isFood()) {
					continue;
				}
				Food food = item.getFood();
				if(bestFood != null) {
					if(food.getHealing() > bestFood.getHealing()) {
						bestFood = food;
						foodSlot = i;
					}
				}else {
					bestFood = food;
					foodSlot = i;
				}
				
			}
			
		    if(bestFood != null) {
		    	mc.player.inventory.currentItem = foodSlot;
		    	mc.gameSettings.keyBindUseItem.setPressed(true);
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
