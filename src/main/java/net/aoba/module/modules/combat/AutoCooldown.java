/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import java.util.Random;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class AutoCooldown extends Module implements TickListener {
	private static final Random RANDOM = new Random();
	
	private final FloatSetting usePercentage = FloatSetting.builder().id("autocooldown_use_percentage")
			.displayName("Use %").description("Charge percentage at which the item is released.")
			.defaultValue(100f).minValue(25f).maxValue(100f).step(5f).build();

	private final FloatSetting randomness = FloatSetting.builder().id("autocooldown_randomness")
			.displayName("Randomness").description("Randomness of the charge percentage and re-use delay. Used to simulate differences in player input.")
			.defaultValue(2.0f).minValue(0.0f).maxValue(10.0f).step(1.0f).build();

	private InteractionHand previousHand;
	private int useDelay;
	private int releaseTick = -1;

	public AutoCooldown() {
		super("AutoCooldown");
		setCategory(Category.of("Combat"));
		setDescription("Automatically triggers items that have a cooldown when held down.");

		addSetting(usePercentage);
		addSetting(randomness);
	}

	@Override
	public void onTick(Pre event) {
		// Automatically re-use the item after a delay.
		if (useDelay > 0) {
			if (--useDelay == 0 && previousHand != null) {
				if (!MC.player.isUsingItem())
					MC.gameMode.useItem(MC.player, previousHand);
				previousHand = null;
			}
			return;
		}

		// Do nothing if the player is NOT using an item.
		if (!MC.player.isUsingItem()) {
			releaseTick = -1;
			return;
		}

		// Gets the use tick that we want to release the item at.
		ItemStack stack = MC.player.getUseItem();
		if (releaseTick < 0) {
			int base;
			if (ModuleUtils.isChargable(stack))
				base = ModuleUtils.getChargeDelay(stack);
			else
				base = stack.getUseDuration(MC.player);

			int target = Math.max(1, (int) Math.ceil(base * usePercentage.getValue() / 100.0));
			releaseTick = target + RANDOM.nextInt(randomness.getValue().intValue() + 1);
		}

		// Use the item if used duration elapsed.
		int used = stack.getUseDuration(MC.player) - MC.player.getUseItemRemainingTicks();
		if (used >= releaseTick) {
			previousHand = MC.player.getUsedItemHand();
			MC.gameMode.releaseUsingItem(MC.player);
			useDelay = 1 + RANDOM.nextInt(randomness.getValue().intValue() + 1);
			IMC.setItemUseCooldown(useDelay + 1);
			releaseTick = -1;
		}
	}

	@Override
	public void onTick(Post event) {

	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		previousHand = null;
		useDelay = 0;
		releaseTick = -1;
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}
}
