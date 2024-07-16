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

/**
 * A class to represent a ClickGui Tab that contains different Components.
 */

package net.aoba.gui.tabs;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.AbstractGui;
import net.aoba.gui.Direction;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.tabs.components.Component;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class AbstractTab extends AbstractGui implements MouseClickListener, MouseMoveListener {
    protected String title;

    protected boolean pinnable = true;
    protected boolean drawBorder = true;

    private BooleanSetting isPinned;
    private Identifier icon = null;

    public AbstractTab(String title, float x, float y, boolean pinnable) {
        super(title + "_tab", x, y, 180, 0);
        this.title = title;

        this.pinnable = pinnable;

        isPinned = new BooleanSetting(title + "_pinned", "IS PINNED", false);
        SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hiddenContainer);
    }

    public AbstractTab(String title, float x, float y, float width, boolean pinnable) {
        super(title + "_tab", x, y, width, 0);
        this.title = title;

        this.pinnable = pinnable;

        isPinned = new BooleanSetting(title + "_pinned", "IS PINNED", false);
        SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hiddenContainer);
    }
    
    public AbstractTab(String title, float x, float y, float width, float height, boolean pinnable) {
        super(title + "_tab", x, y, width, height);
        this.title = title;

        this.pinnable = pinnable;

        isPinned = new BooleanSetting(title + "_pinned", "IS PINNED", false);
        SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hiddenContainer);
    }
    
    public AbstractTab(String title, float x, float y, boolean pinnable, String iconName) {
        super(title + "_tab", x, y, 180, 0);
        this.title = title;

        this.pinnable = pinnable;

        isPinned = new BooleanSetting(title + "_pinned", "IS PINNED", false);
        SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hiddenContainer);
        icon = Identifier.of("aoba", "/textures/" + iconName.trim().toLowerCase() + ".png");
    }

    public final String getTitle() {
        return title;
    }

    public final boolean isPinned() {
        return isPinned.getValue();
    }

    public final void setPinned(boolean pin) {
        this.isPinned.setValue(pin);
    }

    public final void setTitle(String title) {

        this.title = title;
    }

    public final void addChild(Component component) {
        this.children.add(component);
    }


    public void preupdate() {
    }

    public void postupdate() {
    }

    @Override
    public void onChildChanged(IGuiElement updatedChild) {
    	if (this.inheritHeightFromChildren) {
            float tempHeight = 0;
            for (Component child : children) {
                tempHeight += (child.getSize().getHeight());
            }
            this.setHeight(tempHeight);
        }
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        Rectangle pos = position.getValue();
        if(pos.isDrawable()) {
        	
        	float x = pos.getX().floatValue();
        	float y = pos.getY().floatValue();
       	 	float width = pos.getWidth().floatValue();
       	 	float height = pos.getHeight().floatValue();
        	
        	if (drawBorder) {
                // Draws background depending on components width and height
                RenderUtils.drawRoundedBox(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(), GuiManager.backgroundColor.getValue());
                RenderUtils.drawRoundedOutline(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue());

                if (icon != null) {
                    RenderUtils.drawTexturedQuad(matrix4f, icon, x + 8, y + 4, 22, 22, GuiManager.foregroundColor.getValue());
                    RenderUtils.drawString(drawContext, this.title, x + 38, y + 8, GuiManager.foregroundColor.getValue());
                } else
                    RenderUtils.drawString(drawContext, this.title, x + 8, y + 8, GuiManager.foregroundColor.getValue());

                RenderUtils.drawLine(matrix4f, x, y + 30, x + width, y + 30, new Color(0, 0, 0, 100));

                if (this.pinnable) {
                    if (this.isPinned.getValue()) {
                        RenderUtils.drawRoundedBox(matrix4f, x + width - 23, y + 8, 15, 15, GuiManager.roundingRadius.getValue(), new Color(154, 0, 0, 200));
                        RenderUtils.drawRoundedOutline(matrix4f,x + width - 23, y + 8, 15, 15, GuiManager.roundingRadius.getValue(), new Color(0, 0, 0, 200));
                    } else {
                        RenderUtils.drawRoundedBox(matrix4f, x + width - 23, y + 8, 15, 15, GuiManager.roundingRadius.getValue(), new Color(128, 128, 128, 50));
                        RenderUtils.drawRoundedOutline(matrix4f, x + width - 23, y + 8, 15, 15, GuiManager.roundingRadius.getValue(), new Color(0, 0, 0, 50));
                    }
                }
            }
            for (Component child : children) {
                child.draw(drawContext, partialTicks);
            }
        }
    }

    @Override
    public void OnMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            float mouseX = (float)event.mouseX;
            float mouseY = (float)event.mouseY;
            
            Rectangle pos = position.getValue();

            if(pos.isDrawable()) {
            	
            	float x = pos.getX().floatValue();
            	float y = pos.getY().floatValue();
           	 	float width = pos.getWidth().floatValue();
           	 	
            	if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
            		
            		if (resizeable) {
    					Rectangle topHitbox = new Rectangle(pos.getX(), pos.getY() - 8, pos.getWidth(), 8.0f);
    					Rectangle leftHitbox = new Rectangle(pos.getX() - 8, pos.getY(), 8.0f, pos.getHeight());
    					Rectangle rightHitbox = new Rectangle(pos.getX() + pos.getWidth(), pos.getY(), 8.0f,
    							pos.getHeight());
    					Rectangle bottomHitbox = new Rectangle(pos.getX(), pos.getY() + pos.getHeight(), pos.getWidth(),
    							8.0f);

    					if (leftHitbox.intersects(mouseX, mouseY))
    						setResizing(true, event, Direction.Left);
    					else if (rightHitbox.intersects(mouseX, mouseY))
    						setResizing(true, event, Direction.Right);
    					else if (topHitbox.intersects(mouseX, mouseY))
    						setResizing(true, event, Direction.Top);
    					else if (bottomHitbox.intersects(mouseX, mouseY))
    						setResizing(true, event, Direction.Bottom);
    					else
    						setResizing(false, event, Direction.None);
    				}

    				if (!isResizing) {
    					boolean allowMove = true;
    					// If the GUI is pinnable, allow the user to click the pin button to pin a gui
                        if (pinnable) {
                        	Rectangle rect = new Rectangle(x + width - 24, (y + 4), 18.0f, 18.0f);
                        	if(rect.intersects(mouseX, mouseY)) {
                        		isPinned.silentSetValue(!isPinned.getValue());
                                isMoving = false;
                                isResizing = false;
                                allowMove = false;
                                event.cancel();
                        	}
                        }
                        
    					 // Allow the user to move the clickgui if it within the header bar and NOT pinned.
                        if (allowMove && !isPinned.getValue()) {
                        	Rectangle rect = new Rectangle(x, y, width, 24.0f);
                        	if(rect.intersects(mouseX, mouseY)) {
                        		 isMoving = true;
                                 isResizing = false;
                                 GuiManager.setCursor(CursorStyle.Click);
                                 event.cancel();
                        	}
                        }
    				}
                }
            }
        }else if (event.button == MouseButton.LEFT && event.action == MouseAction.UP) {
			isMoving = false;
			isResizing = false;
			GuiManager.setCursor(CursorStyle.Default);
		}
    }

	@Override
	public void update() {

	}
}
