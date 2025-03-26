/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class FastPlace extends Module implements TickListener {

	private final FloatSetting speed = FloatSetting.builder().id("fastplace_delay").displayName("Delay")
			.description("Delay at which blocks are placed in ticks..").defaultValue(0f).minValue(0f).maxValue(5f)
			.step(1f).build();

	public FastPlace() {
		super("FastPlace");

		setCategory(Category.of("Misc"));
		setDescription("Places blocks exceptionally fast");

		addSetting(speed);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity
		);
	}

	@Override
	public void onDisable() {
		IMC.setItemUseCooldown(5);
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
		int currentItemCooldown = IMC.getItemUseCooldown();
		int speedValue = speed.getValue().intValue();
		if (currentItemCooldown == 0 || currentItemCooldown > speedValue)
			IMC.setItemUseCooldown(speedValue);
	}

	@Override
	public void onTick(Post event) {

	}
}
