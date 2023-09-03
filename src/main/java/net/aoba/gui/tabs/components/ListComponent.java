package net.aoba.gui.tabs.components;

import java.util.ArrayList;
import java.util.List;

import net.aoba.Aoba;
import net.aoba.core.settings.types.IndexedStringListSetting;
import net.aoba.event.events.MouseLeftClickEvent;
import net.aoba.event.listeners.MouseLeftClickListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ListComponent extends Component implements MouseLeftClickListener {

	private String text;
	private boolean wasClicked = false;
	private IndexedStringListSetting list;
	private List<Component> settingsList = new ArrayList<Component>();
	
	public ListComponent(String text, ClickGuiTab parent) {
		super();
		this.text = text;
		this.parent = parent;
		
		Aoba.getInstance().eventManager.AddListener(MouseLeftClickListener.class, this);
	}

	public ListComponent(ClickGuiTab parent, IndexedStringListSetting list) {
		super();
		this.text = list.displayName;
		this.parent = parent;
		this.list = list;
		
		Aoba.getInstance().eventManager.AddListener(MouseLeftClickListener.class, this);
	}

	@Override
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();
		int length = MinecraftClient.getInstance().textRenderer.getWidth(list.getIndexValue());
		MatrixStack matrixStack = drawContext.getMatrices();
		renderUtils.drawOutlinedBox(matrixStack, parentX + 4, parentY + offset, parentWidth - 8, 22, new Color(25,25,25),
				0.3f);
		renderUtils.drawString(drawContext, list.getIndexValue(), (parentX + (parentWidth / 2)) - length , parentY + offset + 4, 0xFFFFFF);
		renderUtils.drawString(drawContext,"<<", parentX + 8, parentY + offset + 4, 0xFFFFFF);
		renderUtils.drawString(drawContext,">>", parentX + 8 + (parentWidth - 34), parentY + offset + 4, 0xFFFFFF);
	}

	@Override
	public void OnMouseLeftClick(MouseLeftClickEvent event) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();
		
		int mouseX = event.GetMouseX();
		int mouseY = event.GetMouseY();
		
		if (HudManager.currentGrabbed == null) {
			if (!wasClicked) {
				if (mouseY >= (((parentY + offset + 4))) && mouseY <= (parentY + offset + 22)) {
					// If Left arrow clicked.
					if (mouseX >= ((parentX + 4)) && mouseX <= ((parentX + 64))) {
						if (!this.wasClicked) {
							list.decrement();
							this.wasClicked = true;
							return;
						}
					}
					// If Right arrow clicked.
					if (mouseX >= ((parentX + parentWidth - 64)) && mouseX <= ((parentX + parentWidth - 4))) {
						if (!this.wasClicked) {
							list.increment();
							this.wasClicked = true;
							return;
						}
					}
				}
			}
		}
	}
}
