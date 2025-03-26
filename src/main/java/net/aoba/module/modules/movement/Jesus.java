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
import net.aoba.settings.types.BooleanSetting;

public class Jesus extends Module implements TickListener {

	private final BooleanSetting legit = BooleanSetting.builder().id("jesus_legit").displayName("Legit")
			.description("Whether or not the player will swim as close to the surface as possible.").defaultValue(true)
			.build();

	public Jesus() {
		super("Jesus");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to walk on water.");
		addSetting(legit);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Karhu,
		    AntiCheat.Buzz
		);
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

	public boolean getLegit() {
		return legit.getValue();
	}

	@Override
	public void onTick(Pre event) {
		// If Legit is enabled, simply swim.
		if (legit.getValue()) {
			if (MC.player.isInLava() || MC.player.isTouchingWater()) {
				MC.options.jumpKey.setPressed(true);
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
