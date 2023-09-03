package net.aoba.gui.tabs.components;

import java.util.ArrayList;
import java.util.List;

import net.aoba.Aoba;
import net.aoba.core.settings.Setting;
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.core.settings.types.IndexedStringListSetting;
import net.aoba.core.settings.types.StringListSetting;
import net.aoba.event.events.MouseLeftClickEvent;
import net.aoba.event.listeners.MouseLeftClickListener;
import net.aoba.module.Module;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ModuleComponent extends Component implements MouseLeftClickListener {

	private String text;
	private Module module;
	private ClickGuiTab parent;
	private boolean wasClicked = false;
	private boolean popped = false;
	

	private int expandedHeight = 30;

	private Color hoverColor = new Color(90, 90, 90);
	private Color color = new Color(128, 128, 128);

	private Color backgroundColor = color;

	private List<Component> settingsList = new ArrayList<Component>();

	public ModuleComponent(String text, ClickGuiTab parent, Module module) {
		super();
		this.text = text;
		this.parent = parent;
		this.module = module;
		for (Setting setting : this.module.getSettings()) {
			Component c;
			if (setting instanceof FloatSetting) {
				c = new SliderComponent(this.parent, (FloatSetting) setting);
			} else if (setting instanceof BooleanSetting) {
				c = new CheckboxComponent(this.parent, (BooleanSetting) setting);
			} else if (setting instanceof StringListSetting) {
				c = new ListComponent(this.parent, (IndexedStringListSetting) setting);
			} else {
				c = null;
			}
			settingsList.add(c);
		}

		RecalculateExpandedHeight();

		Aoba.getInstance().eventManager.AddListener(MouseLeftClickListener.class, this);
	}

	@Override
	public void update(int offset) {
		super.update(offset);

		// If the Module options are popped, display all of the options.
		if (this.popped) {
			// Updates all of the options.
			int i = offset + 30;
			for (Component children : this.settingsList) {
				children.update(i);
				i += children.getHeight();
			}
		}
	}

	@Override
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();
		MatrixStack matrixStack = drawContext.getMatrices();
		// renderUtils.drawOutlinedBox(matrixStack, parentX + 2, parentY + offset,
		// parentWidth - 4, this.getHeight() - 2, backgroundColor, 0.2f);

		if (this.popped) {
			int i = offset + 30;
			for (Component children : this.settingsList) {
				if (children.isVisible()) {
					children.draw(i, drawContext, partialTicks, color);
					i += children.getHeight();
				}
			}
		}

		renderUtils.drawString(drawContext, this.text, parentX + 8, parentY + 8 + offset,
				module.getState() ? 0x00FF00 : 0xFFFFFF);
		if (module.hasSettings()) {
			renderUtils.drawString(drawContext, this.popped ? "<<" : ">>", parentX + parentWidth - 30,
					parentY + 8 + offset, color.getColorAsInt());
		}
	}

	public void RecalculateExpandedHeight() {
		int height = 30;
		for (Component children : this.settingsList) {
			if (children.isVisible()) {
				height += children.getHeight();
			}
		}
		expandedHeight = height;
	}

	@Override
	public void OnMouseLeftClick(MouseLeftClickEvent event) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();

		int mouseX = event.GetMouseX();
		int mouseY = event.GetMouseY();

		if (hovered) {
			backgroundColor = hoverColor;
			boolean isOnOptionsButton = (mouseX >= (parentX + parentWidth - 34) && mouseX <= (parentX + parentWidth));
			if (isOnOptionsButton) {
				this.popped = !this.popped;

				if (this.popped) {
					this.setHeight(expandedHeight);
				} else {
					this.setHeight(30);
				}
			} else {
				module.toggle();
				return;
			}
		} else {
			backgroundColor = color;
		}
	}
}
