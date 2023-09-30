package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.core.settings.SettingManager;
import net.aoba.core.settings.types.Vector2Setting;
import net.aoba.core.utils.types.Vector2;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class AbstractHud implements LeftMouseDownListener, MouseMoveListener {
	protected static RenderUtils renderUtils = new RenderUtils();
	protected static MinecraftClient mc = MinecraftClient.getInstance();

	protected String ID;

	protected Vector2Setting position;
	protected float width;
	protected float height;

	protected boolean isMouseOver = false;
	protected boolean moveable = true;

	// Mouse Variables
	protected double lastClickOffsetX;
	protected double lastClickOffsetY;

	public AbstractHud(String ID, int x, int y, int width, int height) {
		this.ID = ID;

		this.position = new Vector2Setting(ID + "_position", "GUI POS", new Vector2(x, y));
		this.width = width;
		this.height = height;

		SettingManager.register_setting(position, Aoba.getInstance().settingManager.hidden_category);

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
	}

	public float getHeight() {
		return this.height;
	}

	public float getX() {
		return position.getValue().x;
	}

	public float getY() {
		return position.getValue().y;
	}

	public float getWidth() {
		return this.width;
	}

	public void setX(float x) {
		position.setX(x);
	}

	public void setY(float y) {
		position.setY(y);
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
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		// TODO: Before the logic would store the clicked mouse pos, which lead to
		// the choppy, unpredictable nature of moving GUI. Not sure if it is needed
		// anymore as we have raw mouse position.

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

		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			double mouseX = event.GetHorizontal();
			double mouseY = event.GetVertical();

			Vector2 pos = position.getValue();

			if (HudManager.currentGrabbed == this && this.moveable) {
				pos.x = (float) (mouseX - this.lastClickOffsetX);
				pos.y = (float) (mouseY - this.lastClickOffsetY);
			}

			if (mouseX >= pos.x && mouseX <= pos.x + width) {
				if (mouseY >= pos.y && mouseY <= pos.y + height) {
					isMouseOver = true;
				}
			}
		}
	}
}
