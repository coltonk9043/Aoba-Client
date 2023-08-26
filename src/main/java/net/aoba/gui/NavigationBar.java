package net.aoba.gui;

import java.util.ArrayList;
import java.util.List;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class NavigationBar {
	MinecraftClient mc = MinecraftClient.getInstance();

	private List<NavigationPane> options;
	
	private int selectedIndex;
	private RenderUtils renderUtils;

	public NavigationBar() {
		options = new ArrayList<NavigationPane>();
		renderUtils = new RenderUtils();
	}

	public void addPane(NavigationPane pane) {
		options.add(pane);
	}
	
	public int getSelectedIndex() {
		return this.selectedIndex;
	}
	
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		AobaClient aoba = Aoba.getInstance();
		Window window = mc.getWindow();

		int width = 100 * options.size();
		int centerX = (window.getWidth() / 2);

		int x = centerX - (width / 2);
		if (aoba.hudManager.isClickGuiOpen() && HudManager.currentGrabbed == null) {
			if (mouseX >= (x) && mouseX <= (x + width)) {
				if (mouseY >= (25) && mouseY <= (50)) {
					if (mouseClicked) {
						int mouseXInt = (int) mouseX;
						int selection = (mouseXInt - x) / 100; 
						this.selectedIndex = selection;
					}
				}
			}
		}
		
		if(options.size() > 0) {
			options.get(selectedIndex).update(mouseX, mouseY, mouseClicked);
		}
	}

	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		Window window = mc.getWindow();

		int centerX = (window.getWidth() / 2);

		MatrixStack matrixStack = drawContext.getMatrices();

		int width = 100 * options.size();
		renderUtils.drawRoundedBox(matrixStack, centerX - (width / 2), 25, width, 25, 6, new Color(30,30,30), 0.4f);
		renderUtils.drawRoundedOutline(matrixStack, centerX -  (width / 2), 25, width, 25, 6, new Color(0,0,0), 0.8f);

		renderUtils.drawRoundedBox(drawContext.getMatrices(), centerX - (width / 2) + (100 * this.selectedIndex), 25, 100, 25, 5, new Color(150, 150, 150), 0.4f);
			
		for(int i = 0; i < options.size(); i++) {
			NavigationPane pane = options.get(i);
			if(i == selectedIndex) {
				pane.render(drawContext, partialTicks, color);
			}
			renderUtils.drawString(drawContext, pane.title, centerX - (width / 2) + 50 + (100 * i) - mc.textRenderer.getWidth(pane.title), 30, color);
		}
		
	}
}