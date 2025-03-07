/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;

public class Glide extends Module implements TickListener {

	private final FloatSetting fallSpeed = FloatSetting.builder().id("glide_fallspeed").displayName("Fall Speed")
			.description("The speed at which the player will fall.").defaultValue(0.2f).minValue(0.1f).maxValue(2f)
			.step(0.1f).build();

	public Glide() {
		super("Glide");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Allows the player to glide down when in the air. Does not prevent fall damage.");
		this.addSetting(fallSpeed);

		this.setDetectable(AntiCheat.NoCheatPlus);
		this.setDetectable(AntiCheat.Vulcan);
		this.setDetectable(AntiCheat.AdvancedAntiCheat);
		this.setDetectable(AntiCheat.Verus);
		this.setDetectable(AntiCheat.Grim);
		this.setDetectable(AntiCheat.Matrix);
		this.setDetectable(AntiCheat.Karhu);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {
		ClientPlayerEntity player = MC.player;
		if (player.getVelocity().y < 0 && (!player.isOnGround() || !player.isInLava() || !player.isSubmergedInWater()
				|| !player.isHoldingOntoLadder())) {
			player.setVelocity(player.getVelocity().x, Math.max(player.getVelocity().y, -fallSpeed.getValue()),
					player.getVelocity().z);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
