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
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class Sneak extends Module implements TickListener {
	public Sneak() {
		super("Sneak");
		setCategory(Category.of("Movement"));
		setDescription("Makes the player appear like they're sneaking.");
	}

	@Override
	public void onDisable() {
		ClientPlayerEntity player = MC.player;
		if (player != null) {
			MC.options.sneakKey.setPressed(false);
		}
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
		if (player != null) {
			MC.options.sneakKey.setPressed(true);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}