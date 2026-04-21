/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import net.aoba.gui.UIElement;
import net.aoba.gui.components.GridComponent;
import net.aoba.gui.components.ItemPreviewComponent;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.gui.types.SizeToContent;
import net.aoba.gui.types.Thickness;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHud extends HudWindow {

	private static final EquipmentSlot[] ARMOR_SLOTS = {
			EquipmentSlot.HEAD,
			EquipmentSlot.CHEST,
			EquipmentSlot.LEGS,
			EquipmentSlot.FEET
	};

	private final ItemPreviewComponent[] slots = new ItemPreviewComponent[ARMOR_SLOTS.length];

	public ArmorHud(int x, int y) {
		super("ArmorHud", x, y);
		setProperty(UIElement.MinWidthProperty, 16f);
		setProperty(UIElement.MinHeightProperty, 64f);
		setProperty(UIElement.PaddingProperty, new Thickness(0f));

		GridComponent grid = new GridComponent();
		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			grid.addRowDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		}
		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			slots[i] = new ItemPreviewComponent();
			grid.addChild(slots[i]);
		}
		setContent(grid);
		setSizeToContent(SizeToContent.None);
	}

	@Override
	public void update() {
		super.update();
		if (MC.player == null)
			return;

		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			ItemStack stack = MC.player.getItemBySlot(ARMOR_SLOTS[i]);
			slots[i].setProperty(ItemPreviewComponent.ItemStackProperty, stack);
		}
	}
}
