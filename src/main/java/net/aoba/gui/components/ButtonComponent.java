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

import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.misc.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class ButtonComponent extends Component {

    private String text;
    private Runnable onClick;
    private Color borderColor = new Color(128, 128, 128);
    private Color backgroundColor = new Color(96, 96, 96);
    private Color hoveredBackgroundColor = new Color(156, 156, 156);

    /**
     * Constructor for button component.
     *
     * @param parent  Parent Tab that this Component resides in.
     * @param text    Text contained in this button element.
     * @param onClick OnClick delegate that will run when the button is pressed.
     */
    public ButtonComponent(IGuiElement parent, String text, Runnable onClick) {
        super(parent, new Rectangle(null, null, null, 38f));

        this.setMargin(new Margin(8f, 4f, 8f, 4f));

        this.text = text;
        this.onClick = onClick;
    }

    public ButtonComponent(IGuiElement parent, String text, Runnable onClick, Color borderColor, Color backgroundColor) {
        super(parent, new Rectangle(null, null, null, 38f));

        this.setMargin(new Margin(8f, 4f, 8f, 4f));

        this.text = text;
        this.onClick = onClick;

        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }
	
    /**
     * Sets the text of the button.
     *
     * @param text Text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the OnClick delegate of the button.
     *
     * @param onClick Delegate to set.
     */
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
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
        float actualHeight = this.getActualSize().getHeight();
        
        if (this.hovered)
        	Render2D.drawOutlinedBox(matrix4f, actualX, actualY, actualWidth, actualHeight, borderColor, hoveredBackgroundColor);
        else
        	Render2D.drawOutlinedBox(matrix4f, actualX , actualY, actualWidth, actualHeight, borderColor, backgroundColor);
        Render2D.drawString(drawContext, this.text, actualX + 6, actualY + 6, 0xFFFFFF);
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
    	super.onMouseClick(event);
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            if (this.hovered) {
            	if( onClick != null)
                	onClick.run();
                event.cancel();
            }
        }
    }
}
