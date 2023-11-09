package net.aoba.gui.tabs;

import net.aoba.Aoba;
import net.aoba.core.utils.types.Vector2;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.hud.AbstractHud;
import net.aoba.gui.tabs.components.CheckboxComponent;
import net.aoba.gui.tabs.components.ColorPickerComponent;
import net.aoba.gui.tabs.components.Component;
import net.aoba.gui.tabs.components.KeybindComponent;
import net.aoba.gui.tabs.components.ListComponent;
import net.aoba.gui.tabs.components.SliderComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.aoba.module.Module;
import net.aoba.settings.Setting;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.IndexedStringListSetting;
import net.aoba.settings.types.StringListSetting;

public class ModuleSettingsTab extends AbstractHud implements LeftMouseDownListener, MouseMoveListener {
	protected String title;
	protected Module module;

	public ModuleSettingsTab(String title, float x, float y, Module module) {
		super(title + "_tab", x, y, 180, 0);
		this.title = title + " Settings";
		this.module = module;
		this.setWidth(260);

		int i = 30;
		KeybindComponent keybindComponent = new KeybindComponent(this, module.getBind());
		keybindComponent.setTop(i);
		keybindComponent.setHeight(30);
		children.add(keybindComponent);

		i += 30;
		for (Setting setting : this.module.getSettings()) {
			Component c;
			if (setting instanceof FloatSetting) {
				c = new SliderComponent(this, (FloatSetting) setting);
			} else if (setting instanceof BooleanSetting) {
				c = new CheckboxComponent(this, (BooleanSetting) setting);
			} else if (setting instanceof StringListSetting) {
				c = new ListComponent(this, (IndexedStringListSetting) setting);
			} else if (setting instanceof ColorSetting) {
				c = new ColorPickerComponent(this, (ColorSetting) setting);
			} else {
				c = null;
			}

			c.setTop(i);
			children.add(c);
			i += c.getHeight();
		}

		this.setHeight(i - 30);
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {

		this.title = title;
	}

	public final void addChild(Component component) {
		this.children.add(component);
	}

	@Override
	public void update() {
		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			for (Component child : this.children) {
				child.update();
			}
		}
	}

	public void preupdate() {
	}

	public void postupdate() {
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();

		Vector2 pos = position.getValue();

		// Draws background depending on components width and height
		renderUtils.drawRoundedBox(matrixStack, pos.x, pos.y, width, height + 30, 6, new Color(30, 30, 30), 0.4f);
		renderUtils.drawRoundedOutline(matrixStack, pos.x, pos.y, width, height + 30, 6, new Color(0, 0, 0), 0.8f);
		renderUtils.drawString(drawContext, this.title, pos.x + 8, pos.y + 8, Aoba.getInstance().hudManager.getColor());
		renderUtils.drawLine(matrixStack, pos.x, pos.y + 30, pos.x + width, pos.y + 30, new Color(0, 0, 0), 0.4f);

		renderUtils.drawBox(matrixStack, pos.x + width - 23, pos.y + 8, 15, 15, color, 1.0f);
		renderUtils.drawLine(matrixStack, pos.x + width - 23, pos.y + 8, pos.x + width - 8, pos.y + 23, new Color(0,0,0), 1.0f);
		renderUtils.drawLine(matrixStack, pos.x + width - 23, pos.y + 23, pos.x + width - 8, pos.y + 8, new Color(0,0,0), 1.0f);
		
		for (Component child : children) {
			child.draw(drawContext, partialTicks, color);
		}
	}

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		super.OnLeftMouseDown(event);

		double mouseX = mc.mouse.getX();
		double mouseY = mc.mouse.getY();
		Vector2 pos = position.getValue();

		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			if (mouseX >= pos.x && mouseX <= pos.x + width) {
				if (mouseY >= pos.y && mouseY <= pos.y + 24) {
					HudManager.currentGrabbed = this;
				}
			}

			if (mouseX >= (pos.x + width - 24) && mouseX <= (pos.x + width - 2)) {
				if (mouseY >= (pos.y + 4) && mouseY <= (pos.y + 20)) {
					HudManager.currentGrabbed = null;
					Aoba.getInstance().hudManager.RemoveHud(this, "Modules");
				}
			}
		}
	}
}
