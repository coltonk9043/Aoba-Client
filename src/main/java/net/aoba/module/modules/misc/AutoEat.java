/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.FoodLevelEvent;
import net.aoba.event.events.ItemUsedEvent;
import net.aoba.event.events.PlayerHealthEvent;
import net.aoba.event.listeners.FoodLevelListener;
import net.aoba.event.listeners.ItemUsedListener;
import net.aoba.event.listeners.PlayerHealthListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoEat extends Module implements FoodLevelListener, PlayerHealthListener, ItemUsedListener {
	private ItemStack lastUsedItemStack = null;
	private int previousSlot = -1;
	private boolean isEating = false;

	private final BooleanSetting swapBack = BooleanSetting.builder().id("autoeat_swap_back").displayName("Swap Back")
			.description("Whether the player's slot will be switched back to their previous slot after eating.")
			.defaultValue(true).build();

	private final BooleanSetting fillToFull = BooleanSetting.builder().id("autoeat_fill_to_hull")
			.displayName("Fill To Full").description("Whether to fill the player's hunger to full.").defaultValue(true)
			.build();

	private final FloatSetting hungerSetting = FloatSetting.builder().id("autoeat_hunger").displayName("Hunger")
			.description("Determines when AutoEat will trigger.").defaultValue(10f).minValue(1f).maxValue(20f).step(1f)
			.build();

	private final FloatSetting healthSetting = FloatSetting.builder().id("autoeat_health").displayName("Health")
			.description("Determines when AutoEat will trigger based on health.").defaultValue(10f).minValue(1f)
			.maxValue(20f).step(1f).build();

	private final BooleanSetting prioritizeGapples = BooleanSetting.builder().id("autoeat_prioritize_gapples")
			.displayName("Prioritize Gapples").description("Prioritizes enchanted golden apples and golden apples.")
			.defaultValue(true).build();

	public AutoEat() {
		super("AutoEat");

		setCategory(Category.of("Misc"));
		setDescription("Automatically eats the best food in your inventory.");

		addSetting(fillToFull);
		addSetting(swapBack);
		addSetting(hungerSetting);
		addSetting(healthSetting);
		addSetting(prioritizeGapples);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(FoodLevelListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(PlayerHealthListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(ItemUsedListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(FoodLevelListener.class, this);
		Aoba.getInstance().eventManager.AddListener(PlayerHealthListener.class, this);
		Aoba.getInstance().eventManager.AddListener(ItemUsedListener.class, this);
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

	private boolean shouldEat() {
		if (isEating && fillToFull.getValue()) {
			return MC.player.getHungerManager().isNotFull();
		} else {
			int foodLevel = MC.player.getHungerManager().getFoodLevel();
			return foodLevel <= hungerSetting.getValue();
		}
	}

	private boolean healthBelowThreshold() {
		float health = MC.player.getHealth();
		return health <= healthSetting.getValue();
	}

	private boolean isCurrentlyHandEdible() {
		Item item = MC.player.getInventory().getSelectedStack().getItem();
		FoodComponent food = item.getComponents().get(DataComponentTypes.FOOD);
		return food != null;
	}

	private void eatIfNecessary() {
		if (MC.player == null)
			return;

		if (shouldEat() || healthBelowThreshold()) {
			// Eat what is in the current hand.
			if (isCurrentlyHandEdible()) {
				MC.options.useKey.setPressed(true);
				isEating = true;
			} else {
				// Else find the hand to eat and start eating.
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
					if (swapBack.getValue()) {
						previousSlot = MC.player.getInventory().getSelectedSlot();
						AobaClient.LOGGER.info("[Aoba] Setting previous slot to: " + previousSlot);
					}

					lastUsedItemStack = MC.player.getInventory().getStack(foodSlot);
					isEating = true;
					AobaClient.LOGGER.info("[Aoba] Eating Slot: " + foodSlot);
					MC.player.getInventory().setSelectedSlot(foodSlot);
					MC.options.useKey.setPressed(true);
				}
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

	@Override
	public void onItemUsed(ItemUsedEvent.Pre event) {

	}

	@Override
	public void onItemUsed(ItemUsedEvent.Post event) {
		AobaClient.LOGGER.info("[Aoba] Item POST");
		if (lastUsedItemStack != null && ItemStack.areItemsEqual(event.getItemStack(), lastUsedItemStack)) {
			AobaClient.LOGGER.info("[Aoba] EATING Item was used");
			boolean shouldContinueEating = shouldEat();
			if (!shouldContinueEating) {
				MC.options.useKey.setPressed(false);
				lastUsedItemStack = null;
				AobaClient.LOGGER.info("[Aoba]No longer eating : " + previousSlot);
				isEating = false;
				if (swapBack.getValue() && previousSlot != -1) {
					AobaClient.LOGGER.info("[Aoba] Swapping back to : " + previousSlot);
					MC.player.getInventory().setSelectedSlot(previousSlot);
					previousSlot = -1;
				}
			}
		}
	}
}