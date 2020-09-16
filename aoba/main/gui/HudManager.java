package aoba.main.gui;

import java.util.Hashtable;
import org.lwjgl.glfw.GLFW;
import aoba.main.gui.elements.ButtonComponent;
import aoba.main.gui.tabs.InfoTab;
import aoba.main.gui.tabs.OptionsTab;
import aoba.main.misc.RenderUtils;
import aoba.main.misc.Utils;
import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class HudManager {

	public Minecraft mc;
	public Hashtable<String, Tab> tabs = new Hashtable<String, Tab>();
	private KeyBinding clickGuiButton = new KeyBinding("key.clickgui", GLFW.GLFW_KEY_GRAVE_ACCENT,
			"key.categories.aoba");
	boolean clickGuiOpen = false;
	private RenderUtils renderUtils = new RenderUtils();
	public static Tab currentGrabbed = null;

	private double lastMouseX = 0;
	private double lastMouseY = 0;

	int color;

	public HudManager() {
		mc = Minecraft.getInstance();
		init();
	}

	public void setColor(int r, int g, int b) {
		this.color = Utils.convertRGBToHex(r, g, b);
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public int getColor() {
		return this.color;
	}

	private void init() {
		int xOffset = 100;

		for (Category category : Module.Category.values()) {
			Tab tab = new Tab(category.name(), xOffset, 10);
			int index = 0;
			for (Module module : mc.aoba.mm.modules) {
				if (module.getCategory() == category) {
					ButtonComponent button = new ButtonComponent(index, module.getName(), tab, module);
					tab.addChild(button);
					index += 1;
				}
			}
			tabs.put(category.name(), tab);
			xOffset += 100;
		}
		InfoTab infoTab = new InfoTab("InfoTab", 100, 200);
		OptionsTab optionsTab = new OptionsTab("Options", 300, 250);
		tabs.put(infoTab.getTitle(), infoTab);
		tabs.put(optionsTab.getTitle(), optionsTab);
	}

	public void update() {
		double mouseX = mc.mouseHelper.getMouseX();
		double mouseY = mc.mouseHelper.getMouseY();
		boolean mouseClicked = mc.mouseHelper.isLeftDown();
		if (this.clickGuiButton.isPressed()) {
			this.clickGuiOpen = !this.clickGuiOpen;
			mc.mouseHelper.toggleMouse();
		}
		if (!mc.mouseHelper.isLeftDown()) {
			currentGrabbed = null;
		}
		if (currentGrabbed != null) {
			currentGrabbed.moveWindow((lastMouseX - mc.mouseHelper.getMouseX()) / 2, (lastMouseY - mc.mouseHelper.getMouseY()) / 2);
		}
		for (Tab tab : tabs.values()) {
			if (clickGuiOpen || tab.getPinned())
				tab.preupdate();
				tab.update(mouseX, mouseY, mouseClicked);
				tab.postupdate();
		}
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
	}

	public void draw() {
		if (this.clickGuiOpen) {
			renderUtils.drawBox(0, 0, 1920, 1080, 0.1f, 0.1f, 0.1f, 0.4f);
		}
		for (Tab tab : tabs.values()) {
			if (clickGuiOpen || tab.getPinned()) {
				tab.draw();
			}
		}
	}

	public boolean isClickGuiOpen() {
		return this.clickGuiOpen;
	}
}
