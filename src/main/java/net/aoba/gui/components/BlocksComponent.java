/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.HashSet;
import java.util.function.Consumer;

import org.joml.Matrix3x2fStack;
import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlocksComponent extends Component implements MouseScrollListener {
	private static final float BLOCK_WIDTH = 32f;
	private static final float BLOCK_MARGIN = 4f;
	private static final float COLLAPSED_HEIGHT = 30f;
	private static final float EXPANDED_HEIGHT = 135f;

	private HashSet<Block> blocks;
	private BlocksSetting blocksSetting;
	private Consumer<HashSet<Block>> onChanged;
	private final String text;
	private int visibleRows;
	private int visibleColumns;
	private int scroll = 0;

	private boolean collapsed = true;

	public BlocksComponent(String text, HashSet<Block> blocks, Consumer<HashSet<Block>> onChanged) {
		this.text = text;
		this.blocks = blocks;
		this.onChanged = onChanged;
	}

	public BlocksComponent(BlocksSetting setting) {
		text = setting.displayName;
		this.blocksSetting = setting;
		this.blocks = setting.getValue();
		this.blocksSetting.addOnUpdate(this::onSettingValueChanged);
	}

	private void onSettingValueChanged(HashSet<Block> b) {
		if (b != this.blocks)
			this.blocks = b;
	}

	public HashSet<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(HashSet<Block> blocks) {
		this.blocks = blocks;
		if (blocksSetting != null)
			blocksSetting.setValue(blocks);
	}

	@Override
	public Size measure(Size availableSize) {
		Size size;
		if (collapsed)
			size = new Size(availableSize.getWidth(), COLLAPSED_HEIGHT);
		else
			size = new Size(availableSize.getWidth(), EXPANDED_HEIGHT);

		visibleColumns = (int) Math.floor((size.getWidth()) / (BLOCK_WIDTH + BLOCK_MARGIN));
		visibleRows = (int) Math.floor((size.getHeight() - 25) / (BLOCK_WIDTH + BLOCK_MARGIN));
		return size;
	}

	/**
	 * Draws the button to the screen.
	 *
	 * @param drawContext  The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 */
	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		Matrix3x2fStack matrixStack = drawContext.pose();

		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();

		Render2D.drawString(drawContext, text, actualX, actualY + 6, 0xFFFFFF);
		Render2D.drawString(drawContext, collapsed ? ">>" : "<<", (actualX + actualWidth - 24), actualY + 6,
				GuiManager.foregroundColor.getValue().getColorAsInt());

		if (!collapsed) {
			matrixStack.pushMatrix();
			matrixStack.scale(2.0f, 2.0f);
			for (int i = scroll; i < visibleRows + scroll; i++) {
				for (int j = 0; j < visibleColumns; j++) {
					int index = (i * visibleColumns) + j;
					if (index > BuiltInRegistries.BLOCK.size())
						continue;

					Block block = BuiltInRegistries.BLOCK.byId(index);

					if (blocks.contains(block)) {
						Render2D.drawBox(drawContext, ((actualX + (j * (BLOCK_WIDTH + BLOCK_MARGIN))) + 1) / 2.0f,
								((actualY + ((i - scroll) * (BLOCK_WIDTH + BLOCK_MARGIN)) + 25)) / 2.0f, BLOCK_WIDTH / 2.0f,
								BLOCK_WIDTH / 2.0f, new Color(0, 255, 0, 55));
					}
					Render2D.drawItem(drawContext, new ItemStack(block.asItem()),
							(int) ((actualX + (j * (BLOCK_WIDTH + BLOCK_MARGIN)) + 2) / 2.0f),
							(int) ((actualY + ((i - scroll) * (BLOCK_WIDTH + BLOCK_MARGIN)) + 25) / 2.0f));
				}
			}

			matrixStack.popMatrix();
		}
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (Aoba.getInstance().guiManager.isClickGuiOpen() && hovered) {
			if (event.GetVertical() > 0 && scroll > 0) {
				scroll--;
			} else if (event.GetVertical() < 0 && (scroll + visibleRows) < (BuiltInRegistries.BLOCK.size() / visibleColumns)) {
				scroll++;
			}
			event.cancel();
		}
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (isVisible())
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
						if (index > BuiltInRegistries.BLOCK.size())
							return;

						Block block = BuiltInRegistries.BLOCK.byId(index);
						if (block != null) {
							if (blocks.contains(block))
								blocks.remove(block);
							else
								blocks.add(block);

							if (blocksSetting != null)
								blocksSetting.setValue(blocks);
							if (onChanged != null)
								onChanged.accept(blocks);

							event.cancel();
						}
					}
				}
			}
		}
	}
}
