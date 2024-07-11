package net.aoba.gui.tabs;

import java.util.HashMap;

import net.aoba.gui.tabs.components.CheckboxComponent;
import net.minecraft.server.network.ServerPlayerEntity;

public class FriendsTab extends AbstractTab {

	private HashMap<ServerPlayerEntity, CheckboxComponent> checkboxes = new HashMap<ServerPlayerEntity, CheckboxComponent>();
	private boolean isDirty = false;
	
	public FriendsTab(String title, int x, int y, boolean pinnable) {
		super(title, x, y, pinnable);
	}

	
}
