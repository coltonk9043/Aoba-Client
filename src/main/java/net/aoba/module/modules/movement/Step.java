/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * Step Module
 */
package net.aoba.module.modules.movement;

import net.aoba.module.Category;
import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step extends Module {

	private FloatSetting stepHeight;
	
	public Step() {
		super(new KeybindSetting("key.step", "Step Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("Step");
        this.setCategory(Category.of("Movement"));
		this.setDescription("Steps up blocks.");
		
		stepHeight = new FloatSetting("step_height", "Height", "Height that the player will step up.", 1f, 0.0f, 2f, 0.5f);
		
		stepHeight.setOnUpdate((i) -> {
			if(this.getState()) {
				EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
				attribute.setBaseValue(stepHeight.getValue());
			}
		});
		
		this.addSetting(stepHeight);
	}

	@Override
	public void onDisable() {
		if(MC.player != null) {
			EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
			attribute.setBaseValue(0.5f);
		}
	}

	@Override
	public void onEnable() {
		EntityAttributeInstance attribute = MC.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT);
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
