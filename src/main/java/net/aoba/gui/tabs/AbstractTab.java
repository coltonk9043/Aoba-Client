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

    public final boolean isGrabbed() {
        return (GuiManager.currentGrabbed == this);
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
                RenderUtils.drawRoundedBox(matrix4f, x, y, width, height, 6, GuiManager.backgroundColor.getValue());
                RenderUtils.drawRoundedOutline(matrix4f, x, y, width, height, 6, GuiManager.borderColor.getValue());

                if (icon != null) {
                    RenderUtils.drawTexturedQuad(matrix4f, icon, x + 8, y + 4, 22, 22, GuiManager.foregroundColor.getValue());
                    RenderUtils.drawString(drawContext, this.title, x + 38, y + 8, GuiManager.foregroundColor.getValue());
                } else
                    RenderUtils.drawString(drawContext, this.title, x + 8, y + 8, GuiManager.foregroundColor.getValue());

                RenderUtils.drawLine(matrix4f, x, y + 30, x + width, y + 30, new Color(0, 0, 0, 100));

                if (this.pinnable) {
                    if (this.isPinned.getValue()) {
                        RenderUtils.drawRoundedBox(matrix4f, x + width - 23, y + 8, 15, 15, 6f, new Color(154, 0, 0, 200));
                        RenderUtils.drawRoundedOutline(matrix4f,x + width - 23, y + 8, 15, 15, 6f, new Color(0, 0, 0, 200));
                    } else {
                        RenderUtils.drawRoundedBox(matrix4f, x + width - 23, y + 8, 15, 15, 6f, new Color(128, 128, 128, 50));
                        RenderUtils.drawRoundedOutline(matrix4f, x + width - 23, y + 8, 15, 15, 6f, new Color(0, 0, 0, 50));
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
            double mouseX = mc.mouse.getX();
            double mouseY = mc.mouse.getY();
            Rectangle pos = position.getValue();

            if(pos.isDrawable()) {
            	
            	float x = pos.getX().floatValue();
            	float y = pos.getY().floatValue();
           	 	float width = pos.getWidth().floatValue();
           	 	
            	if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
                    // Allow the user to move the clickgui if it within the header bar and NOT pinned.
                    if (!isPinned.getValue()) {
                        if (mouseX >= x && mouseX <= x + width) {
                            if (mouseY >= y && mouseY <= y + 24) {
                                GuiManager.currentGrabbed = this;
                                isMoving = true;
                            }
                        }
                    }

                    // If the GUI is pinnable, allow the user to click the pin button to pin a gui
                    if (pinnable) {
                        if (mouseX >= (x + width - 24) && mouseX <= (x + width - 2)) {
                            if (mouseY >= (y + 4) && mouseY <= (y + 20)) {
                                GuiManager.currentGrabbed = null;
                                isPinned.silentSetValue(!isPinned.getValue());
                            }
                        }
                    }
                    
                    if(resizeable) {
                    	if(mouseX <= pos.getX() && mouseX >= pos.getX() - 8 && mouseY >= pos.getY() && mouseY <= (pos.getY() + pos.getHeight())) 
    						setResizing(true, mouseX, mouseY, Direction.Left);
    					else if(mouseX >= pos.getX() + pos.getWidth() && mouseX <= pos.getX() + pos.getWidth() + 8 && mouseY >= pos.getY() && mouseY <= (pos.getY() + pos.getHeight()))
    						setResizing(true, mouseX, mouseY, Direction.Right);
    					else if(mouseY <= pos.getY() && mouseY >= pos.getY() - 8 && mouseX >= pos.getX() && mouseX <= (pos.getX() + pos.getWidth()))
    						setResizing(true, mouseX, mouseY, Direction.Top);
    					else if(mouseY >= pos.getY() + pos.getHeight() && mouseY <= pos.getY() + pos.getHeight() + 8 && mouseX >= pos.getX() && mouseX <= (pos.getX() + pos.getWidth()))
    						setResizing(true, mouseX, mouseY, Direction.Bottom);
    					else
    						setResizing(false, mouseX, mouseY, Direction.None);
    				}
                }
            }
        }
    }

	@Override
	public void update() {

	}
}
