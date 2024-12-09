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

package net.aoba.gui.components;

import org.joml.Matrix4f;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class BlocksComponent extends Component implements MouseScrollListener {
	private static final float BLOCK_WIDTH = 32f;
	private static final float BLOCK_MARGIN = 4f;
	private static final float COLLAPSED_HEIGHT = 30f;
	private static final float EXPANDED_HEIGHT = 135f;

	private BlocksSetting blocks;
	private String text;
	private int visibleRows;
	private int visibleColumns;
	private int scroll = 0;

	private boolean collapsed = true;

	/**
	 * Constructor for button component.
	 *
	 * @param parent Parent Tab that this Component resides in.
	 */
	public BlocksComponent(BlocksSetting setting) {
		super();
		this.text = setting.displayName;
		blocks = setting;

		this.setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void measure(Size availableSize) {
		if (collapsed)
			preferredSize = new Size(availableSize.getWidth(), COLLAPSED_HEIGHT);
		else
			preferredSize = new Size(availableSize.getWidth(), EXPANDED_HEIGHT);

		visibleColumns = (int) Math.floor((preferredSize.getWidth()) / (BLOCK_WIDTH + BLOCK_MARGIN));
		visibleRows = (int) Math.floor((preferredSize.getHeight() - 25) / (BLOCK_WIDTH + BLOCK_MARGIN));
	}

	/**
	 * Draws the button to the screen.
	 *
	 * @param drawContext  The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 */
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();

		Render2D.drawString(drawContext, text, actualX, actualY + 6, 0xFFFFFF);
		Render2D.drawString(drawContext, collapsed ? ">>" : "<<", (actualX + actualWidth - 24), actualY + 6,
				GuiManager.foregroundColor.getValue().getColorAsInt());

		if (!collapsed) {
			matrixStack.push();
			matrixStack.scale(2.0f, 2.0f, 2.0f);
			for (int i = scroll; i < visibleRows + scroll; i++) {
				for (int j = 0; j < visibleColumns; j++) {
					int index = (i * visibleColumns) + j;
					if (index > Registries.BLOCK.size())
						continue;

					Block block = Registries.BLOCK.get(index);

					if (blocks.getValue().contains(block)) {
						Render2D.drawBox(matrix4f, ((actualX + (j * (BLOCK_WIDTH + BLOCK_MARGIN))) + 1),
								((actualY + ((i - scroll) * (BLOCK_WIDTH + BLOCK_MARGIN)) + 25)), BLOCK_WIDTH,
								BLOCK_WIDTH, new Color(0, 255, 0, 55));
					}
					Render2D.drawItem(drawContext, new ItemStack(block.asItem()),
							(int) ((actualX + (j * (BLOCK_WIDTH + BLOCK_MARGIN)) + 2) / 2.0f),
							(int) ((actualY + ((i - scroll) * (BLOCK_WIDTH + BLOCK_MARGIN)) + 25) / 2.0f));
				}
			}

			matrixStack.pop();
		}
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (Aoba.getInstance().guiManager.isClickGuiOpen() && this.hovered) {
			if (event.GetVertical() > 0 && scroll > 0) {
				scroll--;
			} else if (event.GetVertical() < 0 && (scroll + visibleRows) < (Registries.BLOCK.size() / visibleColumns)) {
				scroll++;
			}
			event.cancel();
		}
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (this.isVisible())
			Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
		else
			Aoba.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				float actualX = actualSize.getX();
				float actualY = actualSize.getY();
				float actualWidth = actualSize.getWidth();
				float actualHeight = actualSize.getHeight();

				Rectangle collapseHitbox = new Rectangle((actualX + 4), actualY, actualWidth, 24.0f);
				if (collapseHitbox.intersects(mouseX, mouseY)) {
					collapsed = !collapsed;
					invalidateMeasure();
					event.cancel();
				} else {
					Rectangle blockHitbox = new Rectangle(actualX + 4, actualY + 24, actualWidth, actualHeight - 24);

					if (blockHitbox.intersects(mouseX, mouseY)) {
						int col = (int) ((mouseX - actualX - 8) / (BLOCK_WIDTH + BLOCK_MARGIN));
						int row = (int) ((mouseY - actualY - 24) / (BLOCK_WIDTH + BLOCK_MARGIN)) + scroll;

						int index = (row * visibleColumns) + col;
						if (index > Registries.BLOCK.size())
							return;

						Block block = Registries.BLOCK.get(index);
						if (block != null) {
							if (this.blocks.getValue().contains(block)) {
								this.blocks.getValue().remove(block);
								this.blocks.update();
							} else {
								this.blocks.getValue().add(block);
								this.blocks.update();
							}

							event.cancel();
						}
					}
				}
			}
		}
	}
}
