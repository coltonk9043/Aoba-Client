/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import java.util.ArrayList;

import org.joml.Matrix3x2fStack;

import com.google.common.collect.Lists;

import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorHud extends HudWindow {

	public ArmorHud(int x, int y) {
		super("ArmorHud", x, y);
		minHeight = 256f;
		maxHeight = 256f;
		minWidth = 16f;
		maxWidth = 16f;
	}

	@Override
	public void draw(Render2D renderer, DrawContext drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = position.getValue();

			if (pos.isDrawable()) {

				// TODO: Don't like this but they removed the armor slot func.
				ArrayList<ItemStack> armors = Lists.newArrayList(MC.player.getInventory().getStack(103),
						MC.player.getInventory().getStack(102), MC.player.getInventory().getStack(101),
						MC.player.getInventory().getStack(100));
				;

				float scale = getActualSize().getHeight() / 64.0f;

				float x1 = pos.getX() / scale;
				float y2 = (pos.getY() + pos.getHeight()) / scale;
				float yOff = 0;

				Matrix3x2fStack matrixStack = drawContext.getMatrices();
				matrixStack.pushMatrix();
				matrixStack.scale(scale, scale);

				for (ItemStack armor : armors) {
					if (armor.getItem() != Items.AIR) {
						// TODO: renderer.drawItem(drawContext, armor, x1, y2 - yOff - 16);
					}
					yOff += (16.0f);
				}
				matrixStack.popMatrix();
			}
		}
		super.draw(renderer, drawContext, partialTicks);
	}
}
