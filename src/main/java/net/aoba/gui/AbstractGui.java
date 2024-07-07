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

package net.aoba.gui;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.tabs.components.Component;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.Vector2Setting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;

public abstract class AbstractGui implements IGuiElement, MouseClickListener, MouseMoveListener {
    protected static MinecraftClient mc = MinecraftClient.getInstance();

    protected String ID;

    protected Vector2Setting position;

    protected float width;
    protected float height;

    protected boolean isMouseOver = false;
    public boolean moveable = true;

    protected boolean alwaysVisible = false;
    protected boolean visible = false;

    // Mouse Variables
    protected double lastClickOffsetX;
    protected double lastClickOffsetY;
    protected boolean inheritHeightFromChildren = true;

    protected ArrayList<Component> children = new ArrayList<>();
    protected boolean isDragging = false;

    public AbstractGui(String ID, float x, float y, float width, float height) {
        this.ID = ID;
        this.position = new Vector2Setting(ID + "_position", ID + "Position", new Vector2(x, y), (Vector2 vec) -> UpdateAll(vec));
        this.width = width;
        this.height = height;
        SettingManager.registerSetting(position, Aoba.getInstance().settingManager.configContainer);
    }

    public void UpdateAll(Vector2 vec) {
        for (Component component : this.children) {
            component.OnParentXChanged();
            component.OnParentYChanged();
        }
    }

    public String getID() {
        return ID;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public float getX() {
        return position.getValue().x;
    }

    @Override
    public float getY() {
        return position.getValue().y;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public void setX(float x) {
        if (this.position.getValue().x != x) {
            position.silentSetX(x);
            for (Component component : this.children) {
                component.OnParentXChanged();
            }
        }
    }

    public void setY(float y) {
        if (this.position.getValue().y != y) {
            position.silentSetY(y);
            for (Component component : this.children) {
                component.OnParentYChanged();
            }
        }
    }

    public void setWidth(float width) {
        if (this.width != width) {
            this.width = width;
            for (Component component : this.children) {
                component.OnParentWidthChanged();
            }
        }
    }

    public void setHeight(float height) {
        if (this.height != height) {
            this.height = height;
            for (Component component : this.children) {
                component.OnParentHeightChanged();
            }
        }
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean state) {
        if (alwaysVisible) state = true;

        if (visible != state) {
            this.visible = state;
            for (Component component : children) {
                component.setVisible(state);
            }

            // Binds/Unbinds respective listeners depending on whether it is visible.
            if (state) {
                Aoba.instance.eventManager.AddListener(MouseClickListener.class, this);
                Aoba.instance.eventManager.AddListener(MouseMoveListener.class, this);
            } else {
                Aoba.instance.eventManager.RemoveListener(MouseClickListener.class, this);
                Aoba.instance.eventManager.RemoveListener(MouseMoveListener.class, this);
            }
        }
    }

    public void setAlwaysVisible(boolean state) {
        this.alwaysVisible = state;
        if (this.alwaysVisible) {
            this.setVisible(true);
        }
    }

    public abstract void update();

    public abstract void draw(DrawContext drawContext, float partialTicks);

    @Override
    public void OnMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            double mouseX = event.mouseX;
            double mouseY = event.mouseY;

            Vector2 pos = position.getValue();

            if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
                if (GuiManager.currentGrabbed == null) {
                    if (mouseX >= pos.x && mouseX <= (pos.x + width) && mouseY >= pos.y && mouseY <= (pos.y + height)) {
                        GuiManager.currentGrabbed = this;
                        this.lastClickOffsetX = mouseX - pos.x;
                        this.lastClickOffsetY = mouseY - pos.y;
                        this.isDragging = true; // Step 2: Set isDragging to true
                    }
                }
            }
        } else if (event.button == MouseButton.LEFT && event.action == MouseAction.UP) {
            if (isDragging) {
                isDragging = false; // Handle mouse release
            }
        }
    }

    @Override
    public void OnMouseMove(MouseMoveEvent event) {
        if (this.visible && Aoba.getInstance().hudManager.isClickGuiOpen()) {
            double mouseX = event.GetHorizontal();
            double mouseY = event.GetVertical();

            Vector2 pos = position.getValue();

            if (GuiManager.currentGrabbed == this && this.moveable) {
                this.setX((float) (mouseX - this.lastClickOffsetX));
                this.setY((float) (mouseY - this.lastClickOffsetY));
            }

            if (mouseX >= pos.x && mouseX <= pos.x + width) {
                if (mouseY >= pos.y && mouseY <= pos.y + height) {
                    isMouseOver = true;
                } else {
                    isMouseOver = false;
                }
            } else {
                isMouseOver = false;
            }
        } else {
            isMouseOver = false;
        }
    }

    @Override
    public void OnChildChanged(IGuiElement child) {

    }
}
