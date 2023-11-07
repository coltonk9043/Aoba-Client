package net.aoba.gui.hud;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.aoba.Aoba;
import net.aoba.core.utils.types.Vector2;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.IHudElement;
import net.aoba.gui.tabs.components.Component;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.Vector2Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class AbstractHud implements IHudElement, LeftMouseDownListener, MouseMoveListener {
	protected static RenderUtils renderUtils = new RenderUtils();
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

	protected ArrayList<Component> children = new ArrayList<>();
	
	public AbstractHud(String ID, float x, float y, float width, float height) {
		this.ID = ID;
		this.position = new Vector2Setting(ID + "_position", "GUI POS", new Vector2(x, y), (Vector2 vec) -> UpdateAll(vec));
		this.width = width;
		this.height = height;
		SettingManager.register_setting(position, Aoba.getInstance().settingManager.hidden_category);
	}

	public void UpdateAll(Vector2 vec) {
		for(Component component : this.children) {
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
		if(this.position.getValue().x != x) {
			position.silentSetX(x);
			for(Component component : this.children) {
				component.OnParentXChanged();
			}
		}
	}

	public void setY(float y) {
		if(this.position.getValue().y != y) {
			position.silentSetY(y);
			for(Component component : this.children) {
				component.OnParentYChanged();
			}
		}
	}

	public void setWidth(float width) {
		if(this.width != width) {
			this.width = width;
			for(Component component : this.children) {
				component.OnParentWidthChanged();
			}
		}
	}

	public void setHeight(float height) {
		if(this.height != height) {
			this.height = height;
			for(Component component : this.children) {
				component.OnParentHeightChanged();
			}
		}
	}
	
	public boolean getVisible() {
		return this.visible;
	}
	
	public void setVisible(boolean state) {
		if(alwaysVisible) state = true;
		
		if(this.visible == state) return;
		
		this.visible = state;
		
		for(Component component : children){
			component.setVisible(state);
		}
		
		// Binds/Unbinds respective listeners depending on whether it is visible.
		if(state) {
			Aoba.instance.eventManager.AddListener(LeftMouseDownListener.class, this);
			Aoba.instance.eventManager.AddListener(MouseMoveListener.class, this);
		}else {
			Aoba.instance.eventManager.RemoveListener(LeftMouseDownListener.class, this);
			Aoba.instance.eventManager.RemoveListener(MouseMoveListener.class, this);
		}
	}
	
	public void setAlwaysVisible(boolean state) {
		this.alwaysVisible = state;
		if(this.alwaysVisible) {
			this.setVisible(true);
		}
	}

	public abstract void update();

	public abstract void draw(DrawContext drawContext, float partialTicks, Color color);

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = event.GetMouseX();
		double mouseY = event.GetMouseY();

		Vector2 pos = position.getValue();

		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= pos.x && mouseX <= (pos.x + width)) {
					if (mouseY >= pos.y && mouseY <= (pos.y + height)) {
						this.lastClickOffsetX = mouseX - pos.x;
						this.lastClickOffsetY = mouseY - pos.y;
					}
				}
			}
		}
	}

	@Override
	public void OnMouseMove(MouseMoveEvent event) {
		if (this.visible && Aoba.getInstance().hudManager.isClickGuiOpen()) {
			double mouseX = event.GetHorizontal();
			double mouseY = event.GetVertical();

			Vector2 pos = position.getValue();

			if (HudManager.currentGrabbed == this && this.moveable) {
				 this.setX((float)(mouseX - this.lastClickOffsetX));
				 this.setY((float) (mouseY - this.lastClickOffsetY));
			}

			if (mouseX >= pos.x && mouseX <= pos.x + width) {
				if (mouseY >= pos.y && mouseY <= pos.y + height) {
					isMouseOver = true;
				}else {
					isMouseOver = false;
				}
			}else {
				isMouseOver = false;
			}
		}else {
			isMouseOver = false;
		}
	}
	
	@Override
	public void OnChildChanged(IHudElement child) {
		
	}
}
