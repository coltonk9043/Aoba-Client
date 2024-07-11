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
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class SliderComponent extends Component implements MouseClickListener, MouseMoveListener {
    private String text;
    private float currentSliderPosition = 0.4f;
    private boolean isSliding = false;

    FloatSetting slider;

    public SliderComponent(String text, IGuiElement parent) {
        super(parent, new Rectangle(null, null, null, 45f));
        this.text = text;
        this.slider = null;
        this.setMargin(new Margin(8f, 2f, 8f, 2f));
    }

    public SliderComponent(IGuiElement parent, FloatSetting slider) {
        super(parent, new Rectangle(null, null, null, 45f));
        this.text = slider.displayName;
        this.slider = slider;
        this.currentSliderPosition = (float) ((slider.getValue() - slider.min_value) / (slider.max_value - slider.min_value));
        this.setMargin(new Margin(8f, 2f, 8f, 2f));
    }
    
	@Override
	public void onVisibilityChanged() {
		if(this.isVisible())
			Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		else
			Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	@Override
	public void onChildChanged(IGuiElement child) {}
	
	@Override
	public void onChildAdded(IGuiElement child) {}

    public float getSliderPosition() {
        return this.currentSliderPosition;
    }

    public void setSliderPosition(float pos) {
        this.currentSliderPosition = pos;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public void OnMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT) {
            if (event.action == MouseAction.DOWN) {
                if (hovered) {
                    isSliding = true;
                }
            } else if (event.action == MouseAction.UP) {
                isSliding = false;
            }
        }
    }

    @Override
    public void OnMouseMove(MouseMoveEvent event) {
        super.OnMouseMove(event);

        if (Aoba.getInstance().hudManager.isClickGuiOpen() && this.isSliding) {
            double mouseX = event.getX();
            
            float actualX = this.getActualSize().getX();
            float actualWidth = this.getActualSize().getWidth();
            
            // Calculate the target position based on the mouse X position
            float targetPosition = (float) Math.min((((mouseX - (actualX)) - 1) / (actualWidth)), 1f);
            targetPosition = Math.max(0f, targetPosition);

            // Interpolate current slider position towards the target position for smoother movement
            this.currentSliderPosition += (targetPosition - this.currentSliderPosition) * 0.1f;

            // Update the slider value based on the new position
            this.slider.setValue((this.currentSliderPosition * (slider.max_value - slider.min_value)) + slider.min_value);
        }
    }


    @Override
    public void update() {
        super.update();
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        // Early exit if slider is null
        if (this.slider == null) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        float actualX = this.getActualSize().getX();
        float actualY = this.getActualSize().getY();
        float actualWidth = this.getActualSize().getWidth();
        
        // Calculate the length of the filled part of the slider
        float sliderProgress = (slider.getValue() - slider.min_value) / (slider.max_value - slider.min_value);
        float filledLength = actualWidth * sliderProgress;

        // Draw the filled part of the slider
        RenderUtils.drawBox(matrix4f, actualX, actualY + 35, filledLength, 2, GuiManager.foregroundColor.getValue());

        // Draw the unfilled part of the slider
        RenderUtils.drawBox(matrix4f, actualX + filledLength, actualY + 35, (actualWidth - filledLength), 2, new Color(255, 255, 255, 255));

        // Draw the slider knob
        RenderUtils.drawCircle(matrix4f, actualX + filledLength, actualY + 35, 6, GuiManager.foregroundColor.getValue());

        // Draw the slider text
        RenderUtils.drawString(drawContext, this.text, actualX, actualY + 8, 0xFFFFFF);

        // Draw the slider value
        String valueText = String.format("%.02f", this.slider.getValue());
        int textSize = mc.textRenderer.getWidth(valueText) * 2;
        RenderUtils.drawString(drawContext, valueText, actualX + actualWidth - 6 - textSize, actualY + 8, 0xFFFFFF);
    }
}
