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

package net.aoba.gui.navigation.windows;

import net.aoba.Aoba;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.navigation.Window;
import net.aoba.misc.Render2D;
import net.aoba.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import java.util.ArrayList;

public class OptionsTab extends Window implements MouseScrollListener {

	int visibleScrollElements;
	int currentScroll;

	public OptionsTab() {
		super("Options", 40, 220, 100, 100);
		Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
	}

	@Override
	public void update() {
		net.minecraft.client.util.Window window = MinecraftClient.getInstance().getWindow();
		this.setWidth(window.getWidth() - 240);
		this.setHeight(window.getHeight() - 240);

		visibleScrollElements = (int) ((this.getSize().getHeight() - 30) / 30);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		Rectangle pos = position.getValue();

		if (pos.isDrawable()) {

			float x = pos.getX().floatValue();
			float y = pos.getY().floatValue();
			float width = pos.getWidth().floatValue();
			float height = pos.getHeight().floatValue();

			Render2D.drawRoundedBox(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(), GuiManager.backgroundColor.getValue());
			Render2D.drawRoundedOutline(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue());
			Render2D.drawLine(matrix4f, x + 480, y, x + 480, y + height, new Color(0, 0, 0, 200));

			ArrayList<Module> modules = Aoba.getInstance().moduleManager.modules;

			int yHeight = 30;
			for (int i = currentScroll; i < Math.min(modules.size(), currentScroll + visibleScrollElements); i++) {
				Module module = modules.get(i);
				Render2D.drawString(drawContext, module.getName(), x + 10, y + yHeight,
						GuiManager.foregroundColor.getValue());
				yHeight += 30;
			}
		}
	}

	@Override
	public void OnMouseScroll(MouseScrollEvent event) {
		ArrayList<Module> modules = Aoba.getInstance().moduleManager.modules;

		if (event.GetVertical() > 0)
			this.currentScroll = Math.min(currentScroll + 1, modules.size() - visibleScrollElements - 1);
		else if (event.GetVertical() < 0)
			this.currentScroll = Math.max(currentScroll - 1, 0);
	}
}
