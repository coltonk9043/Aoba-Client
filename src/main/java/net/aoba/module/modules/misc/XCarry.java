package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module implements SendPacketListener {
	public XCarry() {
		super("XCarry");
		this.setCategory(Category.of("Misc"));
		this.setDescription("Allows you to store items in your crafting slot..");
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
		if (packet instanceof CloseHandledScreenC2SPacket) {
			CloseHandledScreenC2SPacket closeScreenPacket = (CloseHandledScreenC2SPacket) packet;
			if (closeScreenPacket.getSyncId() == MC.player.playerScreenHandler.syncId)
				event.cancel();
		}
	}
}
