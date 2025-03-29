/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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

	private final FloatSetting healthTrigger = FloatSetting.builder().id("autototem_health").displayName("Health")
			.description("The health at which the totem will be placed into your hand.").defaultValue(6.0f)
			.minValue(1.0f).maxValue(20.0f).step(1.0f).build();

	private final FloatSetting crystalRadiusTrigger = FloatSetting.builder().id("autototem_crystal_radius")
			.displayName("Crystal Radius")
			.description("The radius at which a placed end crystal will trigger autototem.").defaultValue(6.0f)
			.minValue(1.0f).maxValue(10.0f).step(1.0f).build();

	private final BooleanSetting mainHand = BooleanSetting.builder().id("autototem_mainhand").displayName("Mainhand")
			.description("Places totem in main hand instead of off-hand").defaultValue(false).build();

	public AutoTotem() {
		super("AutoTotem");

		setCategory(Category.of("Combat"));
		setDescription("Automatically replaced totems.");

		addSetting(healthTrigger);
		addSetting(crystalRadiusTrigger);
		addSetting(mainHand);
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

		// If current screen is a generic container, we want to prevent autototem from
		// firing.
		if (mc.currentScreen instanceof GenericContainerScreen)
			return;

		// If the current hand stack is a totem, return;
		PlayerInventory inventory = mc.player.getInventory();
		ItemStack handItemStack = inventory.getSelectedStack();

		if (handItemStack.getItem() == Items.TOTEM_OF_UNDYING)
			return;

		// If the player health is below the Health Trigger, switch to the totem
		if (readPacketEvent.getHealth() <= healthTrigger.getValue()) {
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
			mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP,
					mc.player);
			mc.player.setStackInHand(Hand.OFF_HAND, inventory.getStack(slot));
		}
	}

	@Override
	public void onReceivePacket(ReceivePacketEvent readPacketEvent) {
		// Check to see if the packet is an entity spawn packet, and if the entity is an
		// end crystal.
		if (readPacketEvent.GetPacket() instanceof EntitySpawnS2CPacket spawnEntityPacket) {
			if (spawnEntityPacket.getEntityType() == EntityType.END_CRYSTAL) {
				// Check if the entity is within the range of the player, and switch immediately
				// if so.
				MinecraftClient mc = MinecraftClient.getInstance();

				if (mc.player.getInventory().getSelectedStack().getItem() == Items.TOTEM_OF_UNDYING)
					return;

				if (mc.player.squaredDistanceTo(spawnEntityPacket.getX(), spawnEntityPacket.getY(),
						spawnEntityPacket.getZ()) < Math.pow(crystalRadiusTrigger.getValue(), 2)) {
					SwitchToTotem();
				}
			}
		}
	}
}
