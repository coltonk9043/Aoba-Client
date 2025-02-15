/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step extends Module {

	private final FloatSetting stepHeight = FloatSetting.builder().id("step_height").displayName("Height")
			.description("Height that the player will step up.").defaultValue(1f).minValue(0f).maxValue(2f).step(0.5f)
			.build();

	public Step() {
		super("Step");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Steps up blocks.");

		stepHeight.addOnUpdate((i) -> {
			if (state.getValue()) {
				EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
				attribute.setBaseValue(stepHeight.getValue());
			}
		});

		this.addSetting(stepHeight);

		this.setDetectable(AntiCheat.NoCheatPlus);
		this.setDetectable(AntiCheat.Vulcan);
		this.setDetectable(AntiCheat.AdvancedAntiCheat);
		this.setDetectable(AntiCheat.Verus);
		this.setDetectable(AntiCheat.Grim);
		this.setDetectable(AntiCheat.Matrix);
		this.setDetectable(AntiCheat.Negativity);
		this.setDetectable(AntiCheat.Karhu);
		this.setDetectable(AntiCheat.Buzz);
	}

	@Override
	public void onDisable() {
		if (MC.player != null) {
			EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
			attribute.setBaseValue(0.5f);
		}
	}

	@Override
	public void onEnable() {
		EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
		attribute.setBaseValue(stepHeight.getValue());
	}

	@Override
	public void onToggle() {

	}

	public float getStepHeight() {
		return stepHeight.getValue();
	}

	public void setStepHeight(float height) {
		this.stepHeight.setValue(height);
	}
}
