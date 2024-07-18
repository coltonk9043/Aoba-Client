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

package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.misc.Render2D;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.joml.Matrix4f;

public class BlocksComponent extends Component implements MouseScrollListener, MouseClickListener {

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
     * @param parent  Parent Tab that this Component resides in.
     * @param text    Text contained in this button element.
     * @param onClick OnClick delegate that will run when the button is pressed.
     */
    public BlocksComponent(IGuiElement parent, BlocksSetting setting) {
        super(parent, new Rectangle(null, null, null, COLLAPSED_HEIGHT));
        this.text = setting.displayName;
        blocks = setting;

        this.setMargin(new Margin(4f, null, 4f, null));

        visibleRows = (int) EXPANDED_HEIGHT / 36;
        visibleColumns = (int) (actualSize.getWidth() / 36);
    }

	@Override
	public void onChildChanged(IGuiElement child) {}
	
	@Override
	public void onChildAdded(IGuiElement child) {}
	
    /**
     * Draws the button to the screen.
     *
     * @param offset       The offset (Y location relative to parent) of the Component.
     * @param drawContext  The current draw context of the game.
     * @param partialTicks The partial ticks used for interpolation.
     * @param color        The current Color of the UI.
     */
    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        float actualX = this.getActualSize().getX();
        float actualY = this.getActualSize().getY();
        float actualWidth = this.getActualSize().getWidth();

        Render2D.drawString(drawContext, text, actualX + 6, actualY + 6, 0xFFFFFF);
        Render2D.drawString(drawContext, collapsed ? ">>" : "<<", (actualX + actualWidth - 24), actualY + 6, GuiManager.foregroundColor.getValue().getColorAsInt());

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
                    	Render2D.drawBox(matrix4f, ((actualX + (j * 36))), ((actualY + ((i - scroll) * 36) + 25)), 32, 32, new Color(0, 255, 0, 55));
                    }
                    drawContext.drawItem(new ItemStack(block.asItem()), (int) ((actualX + (j * 36) + 2) / 2.0f), (int) ((actualY + ((i - scroll) * 36) + 25) / 2.0f));
                }
            }

            matrixStack.pop();
        }
    }

    @Override
    public void OnMouseScroll(MouseScrollEvent event) {
        if (Aoba.getInstance().hudManager.isClickGuiOpen() && this.hovered) {
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
        if (this.isVisible()) {
            Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
            Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
        } else {
            Aoba.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
            Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
        }
    }

    @Override
    public void OnMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            if(hovered) {
                double mouseX = event.mouseX;
                double mouseY = event.mouseY;
                
            	float actualX = actualSize.getX();
                float actualY = actualSize.getY();

                if (mouseX > (actualX + 4) && mouseY < (actualX + (36 * visibleColumns) + 4)) {
                    if (mouseY > actualY && mouseY < actualY + 25) {
                        collapsed = !collapsed;
                        if (collapsed) 
                        	this.setHeight(COLLAPSED_HEIGHT);
                        else
                            this.setHeight(EXPANDED_HEIGHT);
                        event.cancel();
                    } else if (mouseY > (actualY + 25) && mouseY < (actualY + (36 * visibleRows) + 25)) {
                        int col = (int) (mouseX - actualX - 8) / 36;
                        int row = (int) ((mouseY - actualY - 24) / 36) + scroll;

                        int index = (row * visibleColumns) + col;
                        if (index > Registries.BLOCK.size())
                            return;

                        Block block = Registries.BLOCK.get(index);
                        if(block != null) {
                            if (this.blocks.getValue().contains(block)) {
                                this.blocks.getValue().remove(block);
                                this.blocks.update();
                            } else {
                                this.blocks.getValue().add(block);
                                this.blocks.update();
                            }
                        }
                    }
                }
                
                event.cancel();
            } 
        }
    }
}
