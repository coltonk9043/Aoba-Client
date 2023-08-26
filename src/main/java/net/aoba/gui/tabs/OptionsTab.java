package net.aoba.gui.tabs;

import java.util.ArrayList;
import net.aoba.Aoba;
import net.aoba.gui.Color;
import net.aoba.gui.hud.AbstractHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.aoba.module.Module;

public class OptionsTab extends AbstractHud {

	int visibleScrollElements;
	int currentScroll;
	
	public OptionsTab() {
		super("Options", 40, 220, 100, 100);
		
		
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		Window window = mc.getWindow();
		this.setWidth(window.getWidth() - 240);
		this.setHeight(window.getHeight() - 240);
		
		visibleScrollElements = (int) ((this.height - 30) / 30);
	}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		
		System.out.println("X: " + this.x + ", Y: " + this.y);
		
		renderUtils.drawRoundedBox(matrixStack, x, y, width, height, 6, new Color(30,30,30), 0.4f);
		renderUtils.drawRoundedOutline(matrixStack, x, y, width, height, 6, new Color(0,0,0), 0.8f);
		
		renderUtils.drawLine(matrixStack, x + 480, y, x + 480, y + height, new Color(0,0,0), 0.8f);
		
		ArrayList<Module> modules = Aoba.getInstance().moduleManager.modules;
		
		int yHeight = 30;
		for(int i = currentScroll; i < Math.min(modules.size(), visibleScrollElements); i++) {
			Module module = modules.get(i);
			renderUtils.drawString(drawContext, module.getName(), this.x + 10, this.y + yHeight, color);
			yHeight += 30;
		}
	}
}
