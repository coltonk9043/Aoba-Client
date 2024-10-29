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
 * autoEat Module
 */
package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.FoodLevelEvent;
import net.aoba.event.events.PlayerHealthEvent;
import net.aoba.event.listeners.FoodLevelListener;
import net.aoba.event.listeners.PlayerHealthListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class AutoEat extends Module implements FoodLevelListener, PlayerHealthListener {
	private FloatSetting hungerSetting = FloatSetting.builder().id("autoeat_hunger").displayName("Hunger")
			.description("Determines when AutoEat will trigger.").defaultValue(10f).minValue(1f).maxValue(20f).step(1f)
			.build();

	private FloatSetting healthSetting = FloatSetting.builder().id("autoeat_health").displayName("Health")
			.description("Determines when AutoEat will trigger based on health.").defaultValue(10f).minValue(1f)
			.maxValue(20f).step(1f).build();

	private BooleanSetting prioritizeGapples = BooleanSetting.builder().id("prioritize_gapples")
			.displayName("Prioritize Gapples").description("Prioritizes enchanted golden apples and golden apples.")
			.defaultValue(true).build();

	public AutoEat() {
		super("AutoEat");

		this.setCategory(Category.of("Misc"));
		this.setDescription("Automatically eats the best food in your inventory.");

		this.addSetting(hungerSetting);
		this.addSetting(healthSetting);
		this.addSetting(prioritizeGapples);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(FoodLevelListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(PlayerHealthListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(FoodLevelListener.class, this);
		Aoba.getInstance().eventManager.AddListener(PlayerHealthListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	public void setHunger(int hunger) {
		hungerSetting.setValue((float) hunger);
	}

	public void setHealth(float health) {
		healthSetting.setValue(health);
	}

	private void eatIfNecessary() {
		if (MC.player.getHungerManager().getFoodLevel() <= hungerSetting.getValue()
				|| MC.player.getHealth() <= healthSetting.getValue()) {
			int foodSlot = -1;
			FoodComponent bestFood = null;

			for (int i = 0; i < 9; i++) {
				Item item = MC.player.getInventory().getStack(i).getItem();
				FoodComponent food = item.getComponents().get(DataComponentTypes.FOOD);
				if (food == null)
					continue;

				if (prioritizeGapples.getValue()) {
					if (item == Items.ENCHANTED_GOLDEN_APPLE) {
						bestFood = food;
						foodSlot = i;
						break;
					} else if (item == Items.GOLDEN_APPLE) {
						bestFood = food;
						foodSlot = i;
					}
				}

				if (bestFood != null) {
					if (food.nutrition() > bestFood.nutrition() && item != Items.GOLDEN_APPLE
							&& item != Items.ENCHANTED_GOLDEN_APPLE) {
						bestFood = food;
						foodSlot = i;
					}
				} else {
					bestFood = food;
					foodSlot = i;
				}
			}

			if (bestFood != null) {
				MC.player.getInventory().selectedSlot = foodSlot;
				MC.options.useKey.setPressed(true);
			}
		}
	}

	@Override
	public void onFoodLevelChanged(FoodLevelEvent readPacketEvent) {
		eatIfNecessary();
	}

	@Override
	public void onHealthChanged(PlayerHealthEvent readPacketEvent) {
		eatIfNecessary();
	}
}