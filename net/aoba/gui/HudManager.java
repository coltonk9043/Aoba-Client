package net.aoba.gui;

import java.util.Hashtable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.aoba.module.Module;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.elements.ModuleComponent;
import net.aoba.gui.tabs.*;
import net.aoba.misc.RainbowColor;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module.Category;
import net.aoba.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class HudManager {

	public MinecraftClient mc;
	public Hashtable<String, ClickGuiTab> tabs = new Hashtable<String, ClickGuiTab>();
	private KeyBinding clickGuiButton = new KeyBinding("key.clickgui", GLFW.GLFW_KEY_GRAVE_ACCENT,
			"key.categories.aoba");
	private boolean clickGuiOpen = false;
	private RenderUtils renderUtils = new RenderUtils();

	public IngameGUI hud;
	public ArmorHUD armorHud;
	public static Tab currentGrabbed = null;

	private int lastMouseX = 0;
	private int lastMouseY = 0;

	public InfoTab infoTab;
	public OptionsTab optionsTab;

	public SliderSetting hue = new SliderSetting("Hue", "color_hue", 4, 0, 360, 1);
	public SliderSetting effectSpeed = new SliderSetting("Effect Spd", "color_speed", 4, 1, 20, 0.1);
	public BooleanSetting rainbow = new BooleanSetting("Rainbow", "rainbow_mode");
	public BooleanSetting ah = new BooleanSetting("ArmorHUD", "armorhud_toggle");
	
	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	public HudManager() {
		mc = MinecraftClient.getInstance();
		hud = new IngameGUI();
		armorHud = new ArmorHUD();
		
		color = new Color(hue.getValueFloat());
		currentColor = color;
		rainbowColor = new RainbowColor();
		this.rainbow.setValue(Settings.getSettingBoolean("rainbowUI"));
		
		infoTab = new InfoTab("InfoTab", 100, 200);
		optionsTab = new OptionsTab("Options", 300, 250, hue, rainbow, ah, effectSpeed);
		
		int xOffset = 320;
		for (Category category : Module.Category.values()) {
			ClickGuiTab tab = new ClickGuiTab(category.name(), xOffset, 1);
			for (Module module : Aoba.getInstance().mm.modules) {
				if (module.getCategory() == category) {
					ModuleComponent button = new ModuleComponent(module.getName(), tab, module);
					tab.addChild(button);
				}
			}
			tabs.put(category.name(), tab);
			xOffset += tab.width;
		}
		tabs.put(infoTab.getTitle(), infoTab);
		tabs.put(optionsTab.getTitle(), optionsTab);
	}
	
	public Color getColor() {
		return this.currentColor;
	}
	
	public Color getOriginalColor() {
		return this.color;
	}

	public void update() {
		if (!Aoba.getInstance().isGhosted()) {
			boolean mouseClicked = mc.mouse.wasLeftButtonClicked();
			if (this.clickGuiButton.isPressed()) {
				this.clickGuiOpen = !this.clickGuiOpen;
				this.clickGuiButton.setPressed(false);
				// TODO FIX TOGGLE MOUSE WITH MIXIN
				this.toggleMouse();
			}
			int mouseX = (int) Math.ceil(mc.mouse.getX());
			int mouseY = (int) Math.ceil(mc.mouse.getY());
			hud.update(mouseX, mouseY, mouseClicked);
			if (this.clickGuiOpen) {
				int dx = (int) Math.ceil(mouseX);
				int dy = (int) Math.ceil(mouseY);
				if (!mc.mouse.wasLeftButtonClicked())
					currentGrabbed = null;
				if (currentGrabbed != null)
					currentGrabbed.moveWindow((lastMouseX - dx), (lastMouseY - dy));
				this.lastMouseX = dx;
				this.lastMouseY = dy;
			}
			for (ClickGuiTab tab : tabs.values()) {
				if (clickGuiOpen || tab.getPinned()) {
					tab.preupdate();
					tab.update(mouseX, mouseY, mouseClicked);
					tab.postupdate();
				}
			}
		}
		if(this.rainbow.getValue()) {
			rainbowColor.update(this.effectSpeed.getValueFloat());
			this.currentColor = rainbowColor.getColor();
		}else {
			this.color.setHSV(hue.getValueFloat(), 1f, 1f);
			this.currentColor = color;
		}
		
	}

	public void draw(MatrixStack matrixStack, float partialTicks) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Window window = mc.getWindow();
		matrixStack.push();
		if (this.clickGuiOpen) {
			renderUtils.drawBox(matrixStack, 0, 0, window.getScaledWidth(), window.getScaledHeight(), 0.1f, 0.1f, 0.1f, 0.4f);
		}
		this.hud.draw(matrixStack, partialTicks, this.currentColor);
		for (ClickGuiTab tab : tabs.values()) {
			if (clickGuiOpen || tab.getPinned()) {
				tab.draw(matrixStack, partialTicks, this.currentColor);
			}
		}
		
		GL11.glEnable(GL11.GL_BLEND);
		matrixStack.pop();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public boolean isClickGuiOpen() {
		return this.clickGuiOpen;
	}
	
	// TODO Toggle Mouse
    public void toggleMouse() {
 		if(this.mc.mouse.isCursorLocked()) {
 			this.mc.mouse.unlockCursor();
 		}else {
 			this.mc.mouse.lockCursor();
 		}
 	}
}
