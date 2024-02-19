package net.aoba.gui.tabs.components;

import java.util.ArrayList;
import java.util.List;
import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.IGuiElement;
import net.aoba.settings.types.IndexedStringListSetting;
import net.minecraft.client.gui.DrawContext;

public class ListComponent extends Component implements LeftMouseDownListener {
	private String text;
	private IndexedStringListSetting list;
	private List<Component> settingsList = new ArrayList<Component>();

	public ListComponent(String text, IGuiElement parent) {
		super(parent);
		this.text = text;

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
	}

	public ListComponent(IGuiElement parent, IndexedStringListSetting list) {
		super(parent);
		this.text = list.displayName;
		this.list = list;

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		/*
		 * float parentX = parent.getX(); float parentY = parent.getY(); float
		 * parentWidth = parent.getWidth(); int length =
		 * MinecraftClient.getInstance().textRenderer.getWidth(list.getIndexValue());
		 * MatrixStack matrixStack = drawContext.getMatrices();
		 * renderUtils.drawOutlinedBox(matrixStack, parentX + 4, parentY + offset,
		 * parentWidth - 8, 22, new Color(25, 25, 25), 0.3f);
		 * renderUtils.drawString(drawContext, list.getIndexValue(), (parentX +
		 * (parentWidth / 2)) - length, parentY + offset + 4, 0xFFFFFF);
		 * renderUtils.drawString(drawContext, "<<", parentX + 8, parentY + offset + 4,
		 * 0xFFFFFF); renderUtils.drawString(drawContext, ">>", parentX + 8 +
		 * (parentWidth - 34), parentY + offset + 4, 0xFFFFFF);
		 */
	}

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		/*
		 * float parentX = parent.getX(); float parentY = parent.getY(); float
		 * parentWidth = parent.getWidth();
		 * 
		 * double mouseX = event.GetMouseX(); double mouseY = event.GetMouseY();
		 * 
		 * if (HudManager.currentGrabbed == null) { if (mouseY >= (((parentY + offset +
		 * 4))) && mouseY <= (parentY + offset + 22)) { // If Left arrow clicked. if
		 * (mouseX >= ((parentX + 4)) && mouseX <= ((parentX + 64))) { list.decrement();
		 * return; } // If Right arrow clicked. if (mouseX >= ((parentX + parentWidth -
		 * 64)) && mouseX <= ((parentX + parentWidth - 4))) { list.increment(); return;
		 * } }
		 * 
		 * }
		 */
	}

	@Override
	public void update() {
		super.update();
	}
}
