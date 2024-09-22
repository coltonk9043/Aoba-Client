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

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render2D;
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class TextBoxComponent extends Component implements KeyDownListener {
    private boolean listeningForKey;
    
    @Nullable
    private String title;
    private String text;
    private StringSetting stringSetting;
    
    private boolean isFocused = false;
    private float focusAnimationProgress = 0.0f;
    private Color errorBorderColor = new Color(255, 0, 0);
    private boolean isErrorState = false;

    // Events
    private Consumer<String> onTextChanged;
    
    public TextBoxComponent(IGuiElement parent, String title) {
    	super(parent, new Rectangle(null, null, null, 30f));
    	this.setMargin(new Margin(8f, 2f, 8f, 2f));
    	this.title = title;
    	this.text = "";
    }
    
    public TextBoxComponent(IGuiElement parent, String title, String text) {
    	super(parent, new Rectangle(null, null, null, 30f));
    	this.setMargin(new Margin(8f, 2f, 8f, 2f));
    	
    	this.title = title;
    	this.text = text;
    }
     
    public TextBoxComponent(IGuiElement parent, StringSetting stringSetting) {
        super(parent, new Rectangle(null, null, null, 30f));
        this.setMargin(new Margin(8f, 2f, 8f, 2f));
        
        this.stringSetting = stringSetting;
        this.stringSetting.addOnUpdate(s -> {this.text = s;});
        
        this.title = stringSetting.displayName;
        this.text = stringSetting.getValue();
    }
    
    @Override
    public void update() {
        super.update();
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        float actualX = this.getActualSize().getX();
        float actualY = this.getActualSize().getY();
        float actualWidth = this.getActualSize().getWidth();
        float actualHeight = this.getActualSize().getHeight();

        if (isFocused) {
            focusAnimationProgress = Math.min(1.0f, focusAnimationProgress + partialTicks * 0.1f);
        } else {
            focusAnimationProgress = Math.max(0.0f, focusAnimationProgress - partialTicks * 0.1f);
        }

        Color borderColor = isErrorState ? errorBorderColor : new Color(115 + (int) (140 * focusAnimationProgress), 115, 115, 200);

        Render2D.drawString(drawContext, title, actualX, actualY + 8, 0xFFFFFF);
        Render2D.drawOutlinedRoundedBox(matrix4f, actualX + actualWidth - 150, actualY, 150, actualHeight, 3.0f, borderColor, new Color(115, 115, 115, 200));

        if (text != null && !text.isEmpty()) {
            int visibleStringLength = 120 / 10;
            String visibleString = text.substring(Math.max(0, text.length() - visibleStringLength - 1), text.length());
            Render2D.drawString(drawContext, visibleString, actualX + actualWidth - 145, actualY + 8, 0xFFFFFF);
        }
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        super.onMouseClick(event);
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            if (hovered) {
            	setListeningForKey(true);
                event.cancel();
            } else {
            	setListeningForKey(false);
            }
        }

        isFocused = listeningForKey;
    }

    @Override
    public void OnKeyDown(KeyDownEvent event) {
        if (listeningForKey) {
            int key = event.GetKey();

            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
            	setListeningForKey(false);
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
            	if(!text.isEmpty()) {
            		 text = text.substring(0, text.length() - 1);
            		 if(stringSetting != null)
            			 stringSetting.setValue(text);
            	}
            } else {
                text += "" + (char) key;
                if(stringSetting != null)
                	stringSetting.setValue(text);
            }

            event.cancel();
        }
    }

    public void setErrorState(boolean isError) {
        this.isErrorState = isError;
    }
    
    private void setListeningForKey(boolean state) {
    	listeningForKey = state;
    	if(listeningForKey) {
    		Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
    	}else {
    		Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
    		if(onTextChanged != null) {
    			onTextChanged.accept(text);
    		}
    	}
    }
    
    public void setOnTextChanged(Consumer<String> onTextChanged) {
    	this.onTextChanged = onTextChanged;
    }
}
