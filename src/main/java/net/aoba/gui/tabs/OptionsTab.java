package net.aoba.gui.tabs;

import java.util.ArrayList;
import net.aoba.Aoba;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.AbstractGui;
import net.aoba.gui.Color;
import net.aoba.gui.GuiManager;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.aoba.module.Module;
import net.aoba.utils.types.Vector2;

public class OptionsTab extends AbstractGui implements MouseScrollListener {

	int visibleScrollElements;
	int currentScroll;

	public OptionsTab() {
		super("Options", 40, 220, 100, 100);
		Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
	}

	@Override
	public void update() {
		Window window = mc.getWindow();
		this.setWidth(window.getWidth() - 240);
		this.setHeight(window.getHeight() - 240);

		visibleScrollElements = (int) ((this.height - 30) / 30);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();

		Vector2 pos = position.getValue();

		System.out.println("X: " + pos.x + ", Y: " + pos.y);

		GuiManager hudManager = Aoba.getInstance().hudManager;
		RenderUtils.drawRoundedBox(matrixStack, pos.x, pos.y, width, height, 6, hudManager.backgroundColor.getValue());
		RenderUtils.drawRoundedOutline(matrixStack, pos.x, pos.y, width, height, 6, hudManager.borderColor.getValue());
		RenderUtils.drawLine(matrixStack, pos.x + 480, pos.y, pos.x + 480, pos.y + height, new Color(0, 0, 0, 200));

		ArrayList<Module> modules = Aoba.getInstance().moduleManager.modules;

		int yHeight = 30;
		for (int i = currentScroll; i < Math.min(modules.size(), currentScroll + visibleScrollElements); i++) {
			Module module = modules.get(i);
			RenderUtils.drawString(drawContext, module.getName(), pos.x + 10, pos.y + yHeight, color);
			yHeight += 30;
		}
	}

	@Override
	public void OnMouseScroll(MouseScrollEvent event) {
		 ArrayList<Module> modules = Aoba.getInstance().moduleManager.modules;
		 
		 if(event.GetVertical() > 0) 
			 this.currentScroll = Math.min(currentScroll + 1, modules.size() - visibleScrollElements - 1); 
		 else if(event.GetVertical() < 0) 
			 this.currentScroll = Math.max(currentScroll - 1, 0);
	}
}
