/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.SendMovementPacketEvent;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.listeners.SendMovementPacketListener;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.mixin.interfaces.IPlayerMoveC2SPacket;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger extends Module implements SendPacketListener, SendMovementPacketListener {
	private boolean lastOnGround, ignorePacket;

	private final BooleanSetting sprint = BooleanSetting.builder().id("antihunger_sprint").displayName("Sprint")
			.description("Change sprint packets.").defaultValue(true).build();

	private final BooleanSetting onGround = BooleanSetting.builder().id("antihunger_onground").displayName("On Ground")
			.description("Fakes onGround.").defaultValue(true).build();

	public AntiHunger() {
		super("AntiHunger");
		setCategory(Category.of("Misc"));
		setDescription("Reduces the amount of hunger that is consumed.");

		addSetting(sprint);
		addSetting(onGround);

		setDetectable(
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Negativity,
				AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(SendMovementPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(SendPacketListener.class, this);
		Aoba.getInstance().eventManager.AddListener(SendMovementPacketListener.class, this);

		lastOnGround = MC.player.isOnGround();
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		if (ignorePacket && event.GetPacket() instanceof PlayerMoveC2SPacket) {
			ignorePacket = false;
			return;
		}

		if (MC.player != null) {
			if (MC.player.hasVehicle() || MC.player.isTouchingWater() || MC.player.isSubmergedInWater())
				return;

			if (event.GetPacket() instanceof PlayerMoveC2SPacket packet && onGround.getValue() && MC.player.isOnGround()
					&& MC.player.fallDistance <= 0.0 && !MC.interactionManager.isBreakingBlock()) {
				((IPlayerMoveC2SPacket) packet).setOnGround(false);
			}
		}

		if (event.GetPacket() instanceof ClientCommandC2SPacket packet && sprint.getValue()) {
			if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING)
				event.cancel();
		}
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Pre event) {
		if (MC.player.isOnGround() && !lastOnGround && onGround.getValue()) {
			ignorePacket = true;
		}

		lastOnGround = MC.player.isOnGround();
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Post event) {

	}
}
