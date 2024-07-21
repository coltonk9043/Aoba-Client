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
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.misc.Render2D;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class CheckboxComponent extends Component implements MouseClickListener {
    private String text;
    private BooleanSetting checkbox;
    private Runnable onClick;
    private boolean isHovered = false;
    private float animationProgress = 0.0f;
    private Color hoverBorderColor = new Color(255, 255, 255);
    private Color clickAnimationColor = new Color(255, 255, 0);
    
    public CheckboxComponent(IGuiElement parent, BooleanSetting checkbox) {
        super(parent, new Rectangle(null, null, null, 30f));
        this.text = checkbox.displayName;
        this.checkbox = checkbox;

        this.setMargin(new Margin(8f, 2f, 8f, 2f));
    }

    /**
     * Draws the checkbox to the screen.
     *
     * @param drawContext  The current draw context of the game.
     * @param partialTicks The partial ticks used for interpolation.
     */
    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        float actualX = this.getActualSize().getX();
        float actualY = this.getActualSize().getY();
        float actualWidth = this.getActualSize().getWidth();

        // Determine border color based on hover and click state
        Color borderColor = isHovered ? hoverBorderColor : new Color(128, 128, 128);
        if (animationProgress > 0) {
            borderColor = clickAnimationColor;
            animationProgress -= partialTicks; // Decrease animation progress
        }

        // Determine fill color based on checkbox state
        Color fillColor = this.checkbox.getValue() ? new Color(0, 154, 0, 200) : new Color(154, 0, 0, 200);

        Render2D.drawString(drawContext, this.text, actualX, actualY + 8, 0xFFFFFF);
        Render2D.drawOutlinedBox(matrix4f, actualX + actualWidth - 24, actualY + 5, 20, 20, borderColor, fillColor);
    }

    /**
     * Handles updating the Checkbox component.
     */
    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onVisibilityChanged() {
        if (this.isVisible()) {
            Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
        } else {
            Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
        }
    }

    @Override
    public void OnMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            if (hovered) {
                checkbox.toggle();
                animationProgress = 1.0f; // Reset animation progress on click
                if (onClick != null) 
                    onClick.run();
                event.cancel();
            }
        }
    }
}
