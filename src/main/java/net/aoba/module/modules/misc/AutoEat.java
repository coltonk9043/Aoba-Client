/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * autoEat Module
 */
package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.FoodLevelEvent;
import net.aoba.event.listeners.FoodLevelListener;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;

public class AutoEat extends Module implements FoodLevelListener {
	private FloatSetting hungerSetting;
	
	public AutoEat() {
		super(new KeybindSetting("key.autoeat", "AutoEat Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("AutoEat");
		this.setCategory(Category.Misc);
		this.setDescription("Automatically eats the best food in your inventory.");
		
		hungerSetting = new FloatSetting("autoeat_hunger", "Hunger", "Determines when AutoEat will trigger.", 6, 1, 20, 1);
		
		this.addSetting(hungerSetting);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(FoodLevelListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(FoodLevelListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	public void setHunger(int hunger) {
		hungerSetting.setValue((float)hunger);
	}

	@Override
	public void OnFoodLevelChanged(FoodLevelEvent readPacketEvent) {
		if(readPacketEvent.getFoodLevel() <= hungerSetting.getValue()) {
			int foodSlot= -1;
			FoodComponent bestFood = null;
			for(int i = 0; i< 9; i++) {
				Item item = MC.player.getInventory().getStack(i).getItem();
				
				FoodComponent food = item.getComponents().get(DataComponentTypes.FOOD);
				if(food == null)
					continue;
				
				if(bestFood != null) {
					if(food.nutrition() > bestFood.nutrition()) {
						bestFood = food;
						foodSlot = i;
					}
				}else {
					bestFood = food;
					foodSlot = i;
				}
				
			}
			
		    if(bestFood != null) {
		    	MC.player.getInventory().selectedSlot = foodSlot;
		    	MC.options.useKey.setPressed(true);
		    }
		}
	}
}
