package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.core.settings.SettingManager;
import net.aoba.core.settings.types.Vector2Setting;
import net.aoba.core.utils.types.Vector2;
import net.aoba.event.events.MouseLeftClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseLeftClickListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

public abstract class AbstractHud implements MouseLeftClickListener, MouseMoveListener {
	protected static RenderUtils renderUtils = new RenderUtils();
	protected static MinecraftClient mc = MinecraftClient.getInstance();
	
	protected String ID; 
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	protected boolean isMouseOver = false;
	protected boolean moveable = true;
	
	// Mouse Variables
	protected float lastMouseX;
	protected float lastMouseY;
	
	
	private Vector2Setting position_setting;
	private Consumer<Vector2> position_setting_update;

	public AbstractHud(String ID, int x, int y, int width, int height) {
		this.ID = ID;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		position_setting_update = new Consumer<Vector2>() {
			@Override
			public void accept(Vector2 vector2) {
				setX(vector2.x);
				setY(vector2.y);
			}
		};

		position_setting = new Vector2Setting(ID + "_position", "Position", new Vector2(x, y), position_setting_update);
		SettingManager.register_setting(position_setting, Aoba.getInstance().settingManager.hidden_category);
		
		Aoba.getInstance().eventManager.AddListener(MouseLeftClickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public void setX(float x) {
		this.x = x;
		position_setting.silentSetX(x);
	}
	
	public void setY(float y) {
		this.y = y;
		position_setting.silentSetY(y);
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public abstract void update();
	
	public abstract void draw(DrawContext drawContext, float partialTicks, Color color);

	@Override
	public void OnMouseLeftClick(MouseLeftClickEvent event) {
		int mouseX = event.GetMouseX();
		int mouseY = event.GetMouseY();
		
		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= x && mouseX <= (x + width)) {
					if (mouseY >= y && mouseY <= (y + height)) {
						System.out.println(ID + " was clicked!");
						//HudManager.currentGrabbed = this;
						lastMouseX = mouseX;
						lastMouseY = mouseY;
					}
				}
			}
		}
	}
	
	@Override 
	public void OnMouseMove(MouseMoveEvent event) {
		/*
		 * if(Aoba.getInstance().hudManager.isClickGuiOpen()) { float mouseX =
		 * (float)event.GetHorizontal(); float mouseY = (float)event.GetVertical();
		 * 
		 * if(mouseX >= x && mouseX <= x + width) { if(mouseY >= y && mouseY <= y +
		 * height) { isMouseOver = true; if(HudManager.currentGrabbed == this &&
		 * this.moveable) { float deltaX = mouseX - lastMouseX; float deltaY = mouseY -
		 * lastMouseY;
		 * 
		 * this.x = this.x + deltaX; this.y = this.y + deltaY; } } } }
		 */
	}
}
