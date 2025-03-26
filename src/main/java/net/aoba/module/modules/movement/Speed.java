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
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public class Speed extends Module implements TickListener {

	private final FloatSetting speedSetting = FloatSetting.builder().id("speed_setting").displayName("Speed")
			.description("Speed.").defaultValue(0.1f).minValue(0.1f).maxValue(6f).step(0.1f).build();

	public Speed() {
		super("Speed");
		setCategory(Category.of("Movement"));
		setDescription("Modifies the Movement-Speed of the Player");

		speedSetting.addOnUpdate((i) -> {
			if (state.getValue()) {
				EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
				attribute.setBaseValue(speedSetting.getValue());
			}
		});

		addSetting(speedSetting);

		setDetectable(
		    AntiCheat.NoCheatPlus,
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
		MC.options.getFovEffectScale().setValue(Math.min(1.0, Math.max(0.0, 1.0)));
		if (MC.player != null) {
			EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
			attribute.setBaseValue(0.1);
		}
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		MC.options.getFovEffectScale().setValue(Math.min(1.0, Math.max(0.0, 0.0)));
		EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		attribute.setBaseValue(speedSetting.getValue());
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {
		EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		attribute.setBaseValue(speedSetting.getValue());
	}

	@Override
	public void onTick(Post event) {

	}
}
