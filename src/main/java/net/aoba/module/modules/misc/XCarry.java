/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module implements SendPacketListener {
	public XCarry() {
		super("XCarry");
		setCategory(Category.of("Misc"));
		setDescription("Allows you to store items in your crafting slot..");

		isDetectable(AntiCheat.Negativity);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(SendPacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		Packet<?> packet = event.GetPacket();
		if (packet instanceof CloseHandledScreenC2SPacket closeScreenPacket) {
            if (closeScreenPacket.getSyncId() == MC.player.playerScreenHandler.syncId)
				event.cancel();
		}
	}
}
