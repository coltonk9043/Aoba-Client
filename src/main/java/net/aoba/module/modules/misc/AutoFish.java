/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class AutoFish extends Module implements ReceivePacketListener {
	private final BooleanSetting autoSwitch = BooleanSetting.builder().id("autofish_autoswitch").displayName("Auto Switch")
			.description("Automatically switch to fishing rod before casting.").defaultValue(true).build();

	private final BooleanSetting autoToggle = BooleanSetting.builder().id("autofish_autotoggle").displayName("Auto Toggle")
			.description("Automatically toggles off if no fishing rod is found in the hotbar.").defaultValue(false)
			.build();

	public AutoFish() {
		super("AutoFish");

		setCategory(Category.of("Misc"));
		setDescription("Automatically fishes for you.");

		addSetting(autoSwitch);
		addSetting(autoToggle);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);

		FindItemResult rod = find(Items.FISHING_ROD);

		if (autoSwitch.getValue()) {
			if (rod.found() && rod.isHotbar()) {
				swap(rod.slot(), false);
			} else {
				if (!autoToggle.getValue())
					return;

				toggle();
			}
		}
	}

	@Override
	public void onToggle() {

	}

	private void castRod(int count) {
		FindItemResult rod = find(Items.FISHING_ROD);

		if (autoSwitch.getValue()) {
			if (rod.found() && rod.isHotbar()) {
				swap(rod.slot(), false);
			} else {
				if (!autoToggle.getValue())
					return;

				toggle();
			}
		}

		for (int i = 0; i < count; i++) {
			MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);
		}
	}

	@Override
	public void onReceivePacket(ReceivePacketEvent readPacketEvent) {
		Packet<?> packet = readPacketEvent.GetPacket();

		if (packet instanceof PlaySoundS2CPacket soundPacket) {
            if (soundPacket.getSound().value().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH)) {
				castRod(2);
			}
		}
	}
}
