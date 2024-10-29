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
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class MCA extends Module implements MouseClickListener {
	public enum Mode {
		FRIEND, PEARL
	}

	private final EnumSetting<Mode> mode = EnumSetting.<Mode>builder().id("mca_mode").displayName("Mode")
			.description("The mode for the action to run when the middle mouse button is clicked.")
			.defaultValue(Mode.FRIEND).build();

	private int previousSlot = -1;

	public MCA() {
		super("MCA");

		this.setCategory(Category.of("misc"));
		this.setDescription("Middle Click Action");

		this.addSetting(mode);
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
		if (mouseClickEvent.button == MouseButton.MIDDLE) {
			if (mouseClickEvent.action == MouseAction.DOWN) {
				switch (mode.getValue()) {
				case Mode.FRIEND:
					if (MC.targetedEntity == null || !(MC.targetedEntity instanceof PlayerEntity player))
						return;

					if (Aoba.getInstance().friendsList.contains(player)) {
						Aoba.getInstance().friendsList.removeFriend(player);
						mouseClickEvent.cancel();
						sendChatMessage("Removed " + player.getName().getString() + " from friends list.");
					} else {
						Aoba.getInstance().friendsList.addFriend(player);
						mouseClickEvent.cancel();
						sendChatMessage("Added " + player.getName().getString() + " to friends list.");
					}
					break;
				case Mode.PEARL:
					FindItemResult result = find(Items.ENDER_PEARL);
					if (!result.found() || !result.isHotbar())
						return;
					previousSlot = MC.player.getInventory().selectedSlot;
					if (!result.isMainHand()) {
						swap(result.slot(), false);
						MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);
						mouseClickEvent.cancel();
					}
					break;
				}
			} else if (mouseClickEvent.action == MouseAction.UP) {
				if (mode.getValue() == Mode.PEARL && previousSlot != -1) {
					swap(previousSlot, false);
					previousSlot = -1;
					mouseClickEvent.cancel();
				}
			}
		}
	}
}
