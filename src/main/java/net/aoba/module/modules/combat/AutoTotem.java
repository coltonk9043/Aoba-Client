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

import net.aoba.module.Category;
import org.lwjgl.glfw.GLFW;

import net.aoba.Aoba;
import net.aoba.event.events.PlayerHealthEvent;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.PlayerHealthListener;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class AutoTotem extends Module implements PlayerHealthListener, ReceivePacketListener {

	public FloatSetting healthTrigger;
	public FloatSetting crystalRadiusTrigger;
	public BooleanSetting mainHand;
	
	public AutoTotem() {
		super(new KeybindSetting("key.autototem", "AutoTotem Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("AutoTotem");
        this.setCategory(Category.of("Combat"));
		this.setDescription("Automatically replaced totems.");
		
		healthTrigger = new FloatSetting("autototem_health", "Health", "The health at which the totem will be placed into your hand.", 6.0f, 1.0f, 20.0f, 1.0f);
		crystalRadiusTrigger = new FloatSetting("autototem_crystal_radius", "Crystal Radius", "The radius at which a placed end crystal will trigger autototem.", 6.0f, 1.0f, 10.0f, 1.0f);
		mainHand = new BooleanSetting("autototem_mainhand", "Mainhand", "Places totem in main hand instead of off-hand", false);
		
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
	public void OnHealthChanged(PlayerHealthEvent readPacketEvent) {
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
		
		// Search for a Totem of Undying in the player's inventory.
		int slot = -1;
		for(int i = 0; i <= 36; i++)
		{
			ItemStack itemStackToCheck = inventory.getStack(i);
			Item itemToCheck = itemStackToCheck.getItem();
			
			if(itemToCheck == Items.TOTEM_OF_UNDYING) {
				slot = i;
				break;
			}
		}

		// Switches the Totem of Undying to the player's main hand.
		if(slot != -1) {
			mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
			mc.interactionManager.pickFromInventory(slot);
            mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
		}
	}

	@Override
	public void OnReceivePacket(ReceivePacketEvent readPacketEvent) {
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
