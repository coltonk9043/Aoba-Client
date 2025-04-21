/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.EnumSetting;
import net.aoba.utils.FindItemResult;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class MCA extends Module implements MouseClickListener {
	public enum Mode {
		FRIEND, PEARL, FIREWORK
	}

	private final EnumSetting<Mode> mode = EnumSetting.<Mode>builder().id("mca_mode").displayName("Mode")
			.description("The mode for the action to run when the middle mouse button is clicked.")
			.defaultValue(Mode.FRIEND).build();

	private int previousSlot = -1;

	public MCA() {
		super("MCA");

		setCategory(Category.of("misc"));
		setDescription("Middle Click Action");

		addSetting(mode);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onMouseClick(MouseClickEvent mouseClickEvent) {
		if (mouseClickEvent.button != MouseButton.MIDDLE) return;

		if (mouseClickEvent.action == MouseAction.DOWN) {
			switch (mode.getValue()) {
				case FRIEND -> handleFriend(mouseClickEvent);
				case PEARL -> handleItem(mouseClickEvent, Items.ENDER_PEARL);
				case FIREWORK -> handleItem(mouseClickEvent, Items.FIREWORK_ROCKET);
			}
		} else if (mouseClickEvent.action == MouseAction.UP) {
			if ((mode.getValue() == Mode.PEARL || mode.getValue() == Mode.FIREWORK) && previousSlot != -1) {
				swap(previousSlot, false);
				previousSlot = -1;
				mouseClickEvent.cancel();
			}
		}
	}

	private void handleFriend(MouseClickEvent event) {
		if (!(MC.targetedEntity instanceof PlayerEntity player)) return;

		String playerName = player.getName().getString();

		if (Aoba.getInstance().friendsList.contains(player)) {
			Aoba.getInstance().friendsList.removeFriend(player);
			sendChatMessage("Removed " + playerName + " from friends list.");
		} else {
			Aoba.getInstance().friendsList.addFriend(player);
			sendChatMessage("Added " + playerName + " to friends list.");
		}

		event.cancel();
	}

	private void handleItem(MouseClickEvent event, Item item) {
		FindItemResult itemResult = find(item);
		if (!itemResult.found() || !itemResult.isHotbar()) return;

		previousSlot = MC.player.getInventory().getSelectedSlot();

		if (!itemResult.isMainHand()) {
			swap(itemResult.slot(), false);
			MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);
			event.cancel();
		}
	}
}
