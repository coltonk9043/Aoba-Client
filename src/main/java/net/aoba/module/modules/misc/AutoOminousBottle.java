/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2025 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */
package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.ItemUsedEvent;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.ItemUsedListener;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.mixin.interfaces.IBossBarS2CPacket;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

public class AutoOminousBottle extends Module implements ReceivePacketListener, ItemUsedListener {
	private ItemStack lastUsedItemStack = null;
	private int previousSlot = -1;

	private final BooleanSetting swapBack = BooleanSetting.builder().id("auto_ominous_bottle_swap_back")
			.displayName("Swap Back")
			.description(
					"Whether the player's slot will be switched back to their previous slot after drinking the potion.")
			.defaultValue(true).build();

	public AutoOminousBottle() {
		super("AutoOminous");

		setCategory(Category.of("Misc"));
		setDescription("Automatically drinks a ominous potion when a raid ends.");

		addSetting(swapBack);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(ItemUsedListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
		Aoba.getInstance().eventManager.AddListener(ItemUsedListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onReceivePacket(ReceivePacketEvent e) {
		// TODO: This will trigger on any boss bar...
		// Figure out a way to make it only occur on Raids

		Packet<?> packet = e.GetPacket();
		if (packet instanceof BossBarS2CPacket bossPacket) {
			IBossBarS2CPacket iPacket = (IBossBarS2CPacket) bossPacket;
			BossBarS2CPacket.Action action = iPacket.getAction();
			if (action.getType() == BossBarS2CPacket.Type.REMOVE) {
				FindItemResult result = findInHotbar(Items.OMINOUS_BOTTLE);
				if (result.found()) {
					if (swapBack.getValue()) {
						previousSlot = MC.player.getInventory().getSelectedSlot();
					}

					int slot = result.slot();
					MC.player.getInventory().setSelectedSlot(slot);
					lastUsedItemStack = MC.player.getInventory().getStack(slot);
					MC.options.useKey.setPressed(true);
				}
			}
		}
	}

	@Override
	public void onItemUsed(ItemUsedEvent.Pre event) {

	}

	@Override
	public void onItemUsed(ItemUsedEvent.Post event) {
		if (lastUsedItemStack != null) {
			if (lastUsedItemStack == event.getItemStack()) {
				MC.options.useKey.setPressed(false);
				lastUsedItemStack = null;

				if (swapBack.getValue() && previousSlot != -1) {
					MC.player.getInventory().setSelectedSlot(previousSlot);
					previousSlot = -1;
				}
			}
		}
	}
}