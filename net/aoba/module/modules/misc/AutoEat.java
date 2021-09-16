package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;

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
		if(mc.player.getHungerManager().getFoodLevel() <= hunger) {
			int foodSlot= -1;
			FoodComponent bestFood = null;
			for(int i = 0; i< 9; i++) {
				Item item = mc.player.getInventory().getStack(i).getItem();
				
				if(!item.isFood()) {
					continue;
				}
				FoodComponent food = item.getFoodComponent();
				if(bestFood != null) {
					if(food.getHunger() > bestFood.getHunger()) {
						bestFood = food;
						foodSlot = i;
					}
				}else {
					bestFood = food;
					foodSlot = i;
				}
				
			}
			
		    if(bestFood != null) {
		    	mc.player.getInventory().selectedSlot = foodSlot;
		    	mc.options.keyUse.setPressed(true);
		    }
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack) {
		
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
