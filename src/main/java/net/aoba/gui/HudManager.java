package net.aoba.gui;

import java.util.ArrayList;
import java.util.List;

import net.aoba.core.settings.SettingManager;
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.DoubleSetting;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import net.aoba.module.Module;
import net.aoba.Aoba;
import net.aoba.gui.hud.AbstractHud;
import net.aoba.gui.hud.ArmorHud;
import net.aoba.gui.hud.InfoHud;
import net.aoba.gui.hud.ModuleSelectorHud;
import net.aoba.gui.hud.RadarHud;
import net.aoba.gui.tabs.*;
import net.aoba.gui.tabs.components.ModuleComponent;
import net.aoba.misc.RainbowColor;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class HudManager {

	protected MinecraftClient mc = MinecraftClient.getInstance();
	protected RenderUtils renderUtils = new RenderUtils();
	

	private KeyBinding clickGuiButton = new KeyBinding("key.clickgui", GLFW.GLFW_KEY_GRAVE_ACCENT,
			"key.categories.aoba");
	private KeyBinding esc = new KeyBinding("key.esc", GLFW.GLFW_KEY_ESCAPE,
			"key.categories.aoba");

	private boolean clickGuiOpen = false;

	public static IMoveable currentGrabbed = null;

	private int lastMouseX = 0;
	private int lastMouseY = 0;
	private int mouseX;
	private int mouseY;

	private List<AbstractHud> activeHuds = new ArrayList<AbstractHud>();
	
	private boolean wasTildaPressed = false;

	public NavigationBar clickGuiNavBar;

	public DoubleSetting hue = new DoubleSetting("color_hue", "Hue", 4, 0, 360, 1, null);
	public DoubleSetting effectSpeed = new DoubleSetting("color_speed", "Effect Spd", 4, 1, 20, 0.1, null);
	public BooleanSetting rainbow = new BooleanSetting("rainbow_mode", "Rainbow", false, null);
	public BooleanSetting ah = new BooleanSetting("armorhud_toggle", "ArmorHUD", false, null);

	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	public HudManager() {
		mc = MinecraftClient.getInstance();
		
		renderUtils = Aoba.getInstance().renderUtils;
		color = new Color(hue.getValue().floatValue(), 1f, 1f);
		currentColor = color;
		rainbowColor = new RainbowColor();
		// rainbow.setValue(Settings.getSettingBoolean("rainbowUI"));
		// TODO: ^^^^^^^^^^^^^^

		clickGuiNavBar = new NavigationBar();
		
		NavigationPane modulesPane = new NavigationPane("Modules");
		NavigationPane toolsPane = new NavigationPane("Tools");
		NavigationPane hudPane = new NavigationPane("Hud");
		NavigationPane settingsPane = new NavigationPane("Settings");
		
		toolsPane.AddHud(new AuthCrackerTab("Auth Cracker", 810, 500));
		
		ModuleSelectorHud moduleSelector = new ModuleSelectorHud();
		ArmorHud armorHud = new ArmorHud(790, 500, 200, 50);
		RadarHud radarHud = new RadarHud(590, 500, 180, 180);
		InfoHud infoHud = new InfoHud(100, 500);
		
		// TODO: Dumb workaround but I would like to be able to add HUDs through the pane found on the NavBar
		this.activeHuds.add(moduleSelector);
		this.activeHuds.add(armorHud);
		this.activeHuds.add(radarHud);
		this.activeHuds.add(infoHud);
		
		hudPane.AddHud(moduleSelector);
		hudPane.AddHud(armorHud);
		hudPane.AddHud(radarHud);
		hudPane.AddHud(infoHud);
		
		settingsPane.AddHud(new OptionsTab("Options", 370, 500, hue, rainbow, ah, effectSpeed));
		
		int xOffset = 335;
		for (Category category : Module.Category.values()) {
			ClickGuiTab tab = new ClickGuiTab(category.name(), xOffset, 75, true);
			for (Module module : Aoba.getInstance().moduleManager.modules) {
				if (module.getCategory() == category) {
					ModuleComponent button = new ModuleComponent(module.getName(), tab, module);
					tab.addChild(button);
				}
			}
			modulesPane.AddHud(tab);
			xOffset += tab.getWidth() + 10;
		}
		
		clickGuiNavBar.addPane(modulesPane);
		clickGuiNavBar.addPane(toolsPane);
		clickGuiNavBar.addPane(hudPane);
		clickGuiNavBar.addPane(settingsPane);

		SettingManager.register_setting(hue, Aoba.getInstance().settingManager.config_category);
		SettingManager.register_setting(effectSpeed, Aoba.getInstance().settingManager.config_category);
		SettingManager.register_setting(rainbow, Aoba.getInstance().settingManager.config_category);
		SettingManager.register_setting(ah, Aoba.getInstance().settingManager.config_category);
	}

	/**
	 * Getter for the current color used by the GUI for text rendering.
	 * @return Current Color
	 */
	public Color getColor() {
		return this.currentColor;
	}

	
	public Color getOriginalColor() {
		return this.color;
	}

	public void update() {
		boolean mouseClicked = mc.mouse.wasLeftButtonClicked();

		if(!Aoba.getInstance().isGhosted()){

			mouseX = (int) Math.ceil(mc.mouse.getX());
			mouseY = (int) Math.ceil(mc.mouse.getY());
			
			/**
			 * Moves the selected Tab to where the user moves their mouse.
			 */
			if (this.clickGuiOpen) {
				clickGuiNavBar.update(mouseX, mouseY, mouseClicked);
				
				int dx = (int) Math.ceil(mouseX);
				int dy = (int) Math.ceil(mouseY);
				if (!mouseClicked)
					currentGrabbed = null;
				else if (currentGrabbed != null) {
					int newX = Math.max(0, currentGrabbed.getX() - (lastMouseX - dx));
					int newY = Math.max(0, currentGrabbed.getY() - (lastMouseY - dy));

					currentGrabbed.setX(newX);
					currentGrabbed.setY(newY);
				}
				this.lastMouseX = dx;
				this.lastMouseY = dy;
			}

			/**
			 * Updates each of the Tab GUIs that are currently on the screen.
			 */
			for(AbstractHud hud : activeHuds) {
				hud.update(lastMouseX, lastMouseY, mouseClicked);
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
		
		/**
		 * Updates the Color. 
		 * TODO: Remove this and move to event-based.
		 */
		if(this.rainbow.getValue()) {
			rainbowColor.update(this.effectSpeed.getValue().floatValue());
			this.currentColor = rainbowColor.getColor();
		}else {
			this.color.setHSV(hue.getValue().floatValue(), 1f, 1f);
			this.currentColor = color;
		}
	}

	public void draw(DrawContext drawContext, float tickDelta) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(1.0f/mc.options.getGuiScale().getValue(), 1.0f/mc.options.getGuiScale().getValue(), 1.0f);

		Window window = mc.getWindow();
		
		/**
		 * Render ClickGUI and Sidebar
		 */
		if (this.clickGuiOpen) {
			renderUtils.drawBox(matrixStack, 0, 0, window.getWidth(), window.getHeight(), 0.1f, 0.1f, 0.1f, 0.4f);
			clickGuiNavBar.draw(drawContext, tickDelta, this.currentColor);
		}else {
			for(AbstractHud hud : activeHuds) {
				hud.draw(drawContext, tickDelta, this.currentColor);
			}
		}

		
		
		matrixStack.pop();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	/**
	 * Gets whether or not the Click GUI is currently open.
	 * @return State of the Click GUI.
	 */
	public boolean isClickGuiOpen() {
		return this.clickGuiOpen;
	}

	/**
	 * Locks and unlocks the Mouse.
	 */
	public void toggleMouse() {
		if(this.mc.mouse.isCursorLocked()) {
			this.mc.mouse.unlockCursor();
		}else {
			this.mc.mouse.lockCursor();
		}
	}
}
