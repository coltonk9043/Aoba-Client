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
 * AutoTotem Module
 */
package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.PlayerHealthEvent;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.PlayerHealthListener;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoTotem extends Module implements PlayerHealthListener, ReceivePacketListener {

	public FloatSetting healthTrigger= FloatSetting.builder()
			.id("autototem_health")
			.displayName("Health")
			.description("The health at which the totem will be placed into your hand.")
			.defaultValue(6.0f)
			.minValue(1.0f)
			.maxValue(20.0f)
			.step(1.0f)
			.build();
	
	public FloatSetting crystalRadiusTrigger= FloatSetting.builder()
			.id("autototem_crystal_radius")
			.displayName("Crystal Radius")
			.description("The radius at which a placed end crystal will trigger autototem.")
			.defaultValue(6.0f)
			.minValue(1.0f)
			.maxValue(10.0f)
			.step(1.0f)
			.build();
	
	public BooleanSetting mainHand = BooleanSetting.builder()
		    .id("autototem_mainhand")
		    .displayName("Mainhand")
		    .description("Places totem in main hand instead of off-hand")
		    .defaultValue(false)
		    .build();
	
	public AutoTotem() {
    	super("AutoTotem");

        this.setCategory(Category.of("Combat"));
		this.setDescription("Automatically replaced totems.");

		this.addSetting(healthTrigger);
		this.addSetting(crystalRadiusTrigger);
		this.addSetting(mainHand);
	}
	
	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(PlayerHealthListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(PlayerHealthListener.class, this);
		Aoba.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onHealthChanged(PlayerHealthEvent readPacketEvent) {
		MinecraftClient mc = MinecraftClient.getInstance();
		
		// If current screen is a generic container, we want to prevent autototem from firing.
		if (mc.currentScreen instanceof GenericContainerScreen) return;
		
		
		// If the current hand stack is a totem, return;
		PlayerInventory inventory = mc.player.getInventory();
		ItemStack handItemStack = inventory.getStack(inventory.selectedSlot);
		
		if(handItemStack.getItem() == Items.TOTEM_OF_UNDYING)
			return;
	
		
		// If the player health is below the Health Trigger, switch to the totem
		if(readPacketEvent.getHealth() <= healthTrigger.getValue()) {
			SwitchToTotem();
		}
	}

	private void SwitchToTotem() {
		MinecraftClient mc = MinecraftClient.getInstance();

		PlayerInventory inventory = mc.player.getInventory();

		int slot = -1;
		for (int i = 0; i <= 36; i++) {
			ItemStack itemStackToCheck = inventory.getStack(i);
			Item itemToCheck = itemStackToCheck.getItem();

			if (itemToCheck == Items.TOTEM_OF_UNDYING) {
				slot = i;
				break;
			}
		}

		if (slot != -1) {
			mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
			mc.player.setStackInHand(Hand.OFF_HAND, inventory.getStack(slot));
		}
	}


	@Override
	public void onReceivePacket(ReceivePacketEvent readPacketEvent) {
		// Check to see if the packet is an entity spawn packet, and if the entity is an end crystal.
		if (readPacketEvent.GetPacket() instanceof EntitySpawnS2CPacket spawnEntityPacket) {
            if (spawnEntityPacket.getEntityType() == EntityType.END_CRYSTAL) {
            	// Check if the entity is within the range of the player, and switch immediately if so.
            	MinecraftClient mc = MinecraftClient.getInstance();
            	
            	if(mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).getItem() == Items.TOTEM_OF_UNDYING)
        			return;
            	
                if (mc.player.squaredDistanceTo(spawnEntityPacket.getX(), spawnEntityPacket.getY(), spawnEntityPacket.getZ()) < Math.pow(crystalRadiusTrigger.getValue(), 2)) {
                    SwitchToTotem();
                }
            }
        }
	}
}
