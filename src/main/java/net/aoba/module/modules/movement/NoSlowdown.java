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
import net.aoba.mixin.interfaces.IEntity;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.util.math.Vec3d;

public class NoSlowdown extends Module implements TickListener {

	private final FloatSetting slowdownMultiplier = FloatSetting.builder().id("noslowdown_multiplier")
			.displayName("Multiplier").description("NoSlowdown walk speed multiplier.").defaultValue(0f).minValue(0f)
			.maxValue(1f).step(0.1f).build();

	public NoSlowdown() {
		super("NoSlowdown");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Prevents the player from being slowed down by blocks.");

		this.addSetting(slowdownMultiplier);

		this.setDetectable(AntiCheat.NoCheatPlus);
		this.setDetectable(AntiCheat.Vulcan);
		this.setDetectable(AntiCheat.AdvancedAntiCheat);
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
		IEntity playerEntity = (IEntity) MC.player;

		if (!playerEntity.getMovementMultiplier().equals(Vec3d.ZERO)) {
			float multiplier = slowdownMultiplier.getValue();
			if (multiplier == 0.0f) {
				playerEntity.setMovementMultiplier(Vec3d.ZERO);
			} else {
				playerEntity.setMovementMultiplier(Vec3d.ZERO.add(1, 1, 1).multiply(1 / multiplier));
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
