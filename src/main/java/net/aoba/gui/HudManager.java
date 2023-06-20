package net.aoba.gui;

import java.util.Hashtable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import net.aoba.module.Module;
import net.aoba.Aoba;
import net.aoba.gui.elements.ModuleComponent;
import net.aoba.gui.tabs.*;
import net.aoba.misc.RainbowColor;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module.Category;
import net.aoba.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class HudManager {

	public MinecraftClient mc;
	public Hashtable<String, ClickGuiTab> tabs = new Hashtable<String, ClickGuiTab>();
	private KeyBinding clickGuiButton = new KeyBinding("key.clickgui", GLFW.GLFW_KEY_GRAVE_ACCENT,
			"key.categories.aoba");
	private KeyBinding esc = new KeyBinding("key.esc", GLFW.GLFW_KEY_ESCAPE,
			"key.categories.aoba");

	private boolean clickGuiOpen = false;
	private RenderUtils renderUtils;;

	public IngameGUI hud;
	public ArmorHUD armorHud;
	public static Tab currentGrabbed = null;

	private int lastMouseX = 0;
	private int lastMouseY = 0;
	private int mouseX;
	private int mouseY;
	
	private boolean wasTildaPressed = false;
	
	public InfoTab infoTab;
	public OptionsTab optionsTab;
	public RadarTab radarTab;

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
		renderUtils = Aoba.getInstance().renderUtils;
		color = new Color(hue.getValueFloat(), 1f, 1f);
		currentColor = color;
		rainbowColor = new RainbowColor();
		rainbow.setValue(Settings.getSettingBoolean("rainbowUI"));
		
		infoTab = new InfoTab("InfoTab", 100, 200);
		optionsTab = new OptionsTab("Options", 300, 250, hue, rainbow, ah, effectSpeed);
		radarTab = new RadarTab("Radar", 500, 250);
		
		int xOffset = 320;
		for (Category category : Module.Category.values()) {
			ClickGuiTab tab = new ClickGuiTab(category.name(), xOffset, 1);
			for (Module module : Aoba.getInstance().moduleManager.modules) {
				if (module.getCategory() == category) {
					ModuleComponent button = new ModuleComponent(module.getName(), tab, module);
					tab.addChild(button);
				}
			}
			tabs.put(category.name(), tab);
			xOffset += tab.getWidth();
		}
		tabs.put(infoTab.getTitle(), infoTab);
		tabs.put(optionsTab.getTitle(), optionsTab);
		tabs.put(radarTab.getTitle(), radarTab);
	}
	
	public Color getColor() {
		return this.currentColor;
	}
	
	public Color getOriginalColor() {
		return this.color;
	}

	public void update() {
		boolean mouseClicked = mc.mouse.wasLeftButtonClicked();

		if(!Aoba.getInstance().isGhosted()){
			for (ClickGuiTab tab : tabs.values()) {
				if (clickGuiOpen || tab.getPinned()) {
					tab.preupdate();
					tab.update(mouseX, mouseY, mouseClicked);
					tab.postupdate();
				}
			}
			
			if (this.clickGuiButton.isPressed() && !wasTildaPressed) {
				wasTildaPressed = true;
				this.clickGuiOpen = !this.clickGuiOpen;
				this.toggleMouse();
			}else if (!this.clickGuiButton.isPressed()) {
				wasTildaPressed = false;
			}

			if (this.esc.isPressed() && this.clickGuiOpen) {
				this.clickGuiOpen = !this.clickGuiOpen;
				this.toggleMouse();
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

	public void draw(DrawContext drawContext, float tickDelta) {
		if (!Aoba.getInstance().isGhosted()) {
			boolean mouseClicked = mc.mouse.wasLeftButtonClicked();
			mouseX = (int) Math.ceil(mc.mouse.getX()) ;
			mouseY = (int) Math.ceil(mc.mouse.getY()) ;
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
			
		}
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(1.0f/mc.options.getGuiScale().getValue(), 1.0f/mc.options.getGuiScale().getValue(), 1.0f);
		
		Window window = mc.getWindow();
		
		if (this.clickGuiOpen) {
			renderUtils.drawBox(matrixStack, 0, 0, window.getWidth(), window.getHeight(), 0.1f, 0.1f, 0.1f, 0.4f);
		}
		this.hud.draw(drawContext, tickDelta, this.currentColor);
		for (ClickGuiTab tab : tabs.values()) {
			if (clickGuiOpen || tab.getPinned()) {
				tab.draw(drawContext, tickDelta, this.currentColor);
			}
		}
		
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
