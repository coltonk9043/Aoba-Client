package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.core.settings.SettingManager;
import net.aoba.core.settings.osettingtypes.Vector2Setting;
import net.aoba.core.utils.types.Vector2;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.IMoveable;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

public abstract class AbstractHud implements IMoveable {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	protected RenderUtils renderUtils = new RenderUtils();
	protected MinecraftClient mc = MinecraftClient.getInstance();

	private Vector2Setting position_setting;
	private Consumer<Vector2> position_setting_update;

	public AbstractHud(String ID, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		position_setting_update = new Consumer<Vector2>() {
			@Override
			public void accept(Vector2 vector2) {
				setX((int)vector2.x);
				setY((int)vector2.y);
			}
		};

		position_setting = new Vector2Setting(ID + "_position", "Position", new Vector2(x, y), position_setting_update);
		SettingManager.register_setting(position_setting, Aoba.getInstance().settingManager.hidden_category);
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x) && mouseX <= (x + width)) {
					if (mouseY >= (y) && mouseY <= (y + height)) {
						if (mouseClicked) {
							HudManager.currentGrabbed = this;
						}
					}
				}
			}
		}

		position_setting.silentSetX(x);
		position_setting.silentSetY(y);
	}
	
	public abstract void draw(DrawContext drawContext, float partialTicks, Color color);
}
