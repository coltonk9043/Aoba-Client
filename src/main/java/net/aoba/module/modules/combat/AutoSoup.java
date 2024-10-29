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
 * AutoSoup Module
 */
package net.aoba.module.modules.combat;

import com.mojang.logging.LogUtils;

import net.aoba.Aoba;
import net.aoba.event.events.PlayerHealthEvent;
import net.aoba.event.listeners.PlayerHealthListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoSoup extends Module implements PlayerHealthListener {

	private FloatSetting health;
	
	private int previousSlot = -1;
	
	public AutoSoup() {
    	super("AutoSoup");

        this.setCategory(Category.of("Combat"));
		this.setDescription("Automatically consumes soup when health is low. (KitPVP)");
		
		health = FloatSetting.builder()
				.id("autosoup_health")
				.displayName("Min. Health")
				.description("Minimum health that the AutoSoup will trigger.")
				.defaultValue(6f)
				.minValue(1f)
				.maxValue(20f)
				.step(1f)
				.build();

		this.addSetting(health);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(PlayerHealthListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(PlayerHealthListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	public void sortInventory() {
		for(int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
			ItemStack stack = MC.player.getInventory().getStack(i);
			if(stack == null || stack.getItem() == Items.BOWL) {
				int nextSoup = findSoup();
				if(nextSoup >= 0) {
					MC.interactionManager.clickSlot(0, nextSoup, 0, SlotActionType.PICKUP, MC.player);
					MC.interactionManager.clickSlot(0, i, 0, SlotActionType.PICKUP, MC.player);
				}
			}
		}
	}
	
	public int findSoup() {
		for(int i = 0; i < 36; i++)
		{
			ItemStack stack = MC.player.getInventory().getStack(i);
			if(stack != null && stack.getItem() == Items.MUSHROOM_STEW) {
				return i;
			}
		}
		return -1;
	}
	
	public void setHunger(int hunger) {
		
	}

	@Override
	public void onHealthChanged(PlayerHealthEvent readPacketEvent) {
		LogUtils.getLogger().info("autosoup");
		float playerHealth = readPacketEvent.getHealth();
		
		// If the players HP is below the given threshold.
		if(playerHealth < health.getValue()) {
			LogUtils.getLogger().info("autosoup enabled");
			// Find the first item in the hotbar that is a Stew item.
			int foodSlot= -1;
			for(int i = 0; i< PlayerInventory.getHotbarSize(); i++) {
				Item item = MC.player.getInventory().getStack(i).getItem();
				
				// Check if it is Mushroom Soup. TODO: Add component check...
				if(item == Items.MUSHROOM_STEM) {
					foodSlot = i; break; 
				}
			}
			
			// If a Stew item was found, switch to it and use it.
			if(foodSlot >= 0) {
				previousSlot = MC.player.getInventory().selectedSlot;
				
				MC.player.getInventory().selectedSlot = foodSlot;
			    MC.options.useKey.setPressed(true);
			    MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);
			    
			    // Return the player's selected slot back to the previous slot.
				if(previousSlot != -1) {
					MC.options.useKey.setPressed(false);
					MC.player.getInventory().selectedSlot = previousSlot;
					previousSlot = -1;
				}
			}else {
			// Otherwise, sort the inventory to try and find some.
				sortInventory();
			}
		}
	}
}