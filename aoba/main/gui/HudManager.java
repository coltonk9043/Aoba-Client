package aoba.main.gui;

import java.util.Hashtable;
import org.lwjgl.glfw.GLFW;
import aoba.main.gui.elements.ModuleComponent;
import aoba.main.gui.tabs.InfoTab;
import aoba.main.gui.tabs.OptionsTab;
import aoba.main.misc.RainbowColor;
import aoba.main.misc.RenderUtils;
import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import aoba.main.settings.BooleanSetting;
import aoba.main.settings.Settings;
import aoba.main.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class HudManager {

	public Minecraft mc;
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
		mc = Minecraft.getInstance();
		hud = new IngameGUI();
		armorHud = new ArmorHUD();
		
		color = new Color(hue.getValueFloat());
		currentColor = color;
		rainbowColor = new RainbowColor();
		this.rainbow.setValue(Settings.getSettingBoolean("rainbowUI"));
		
		infoTab = new InfoTab("InfoTab", 100, 200);
		optionsTab = new OptionsTab("Options", 300, 250, hue, rainbow, ah, effectSpeed);
		
		int xOffset = 165;
		for (Category category : Module.Category.values()) {
			ClickGuiTab tab = new ClickGuiTab(category.name(), xOffset, 1);
			for (Module module : mc.aoba.mm.modules) {
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
		if (!this.mc.aoba.isGhosted()) {
			boolean mouseClicked = mc.mouseHelper.isLeftDown();
			if (this.clickGuiButton.isPressed()) {
				this.clickGuiOpen = !this.clickGuiOpen;
				mc.mouseHelper.toggleMouse();
			}
			int mouseX = (int) Math.ceil(mc.mouseHelper.getMouseX());
			int mouseY = (int) Math.ceil(mc.mouseHelper.getMouseY());
			hud.update(mouseX, mouseY, mouseClicked);
			if (this.clickGuiOpen) {
				int dx = (int) Math.ceil(mouseX * (double) this.mc.getMainWindow().getScaledWidth()
						/ (double) this.mc.getMainWindow().getWidth());
				int dy = (int) Math.ceil(mouseY * (double) this.mc.getMainWindow().getScaledHeight()
						/ (double) this.mc.getMainWindow().getHeight());
				if (!mc.mouseHelper.isLeftDown())
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

	public void draw(int scaledWidth, int scaledHeight) {
		if (this.clickGuiOpen) {
			renderUtils.drawBox(0, 0, 1920, 1080, 0.1f, 0.1f, 0.1f, 0.4f);
		}
		this.hud.draw(scaledWidth, scaledHeight, this.currentColor);
		for (ClickGuiTab tab : tabs.values()) {
			if (clickGuiOpen || tab.getPinned()) {
				tab.draw(scaledWidth, scaledHeight, this.currentColor);
			}
		}
	}

	public boolean isClickGuiOpen() {
		return this.clickGuiOpen;
	}
}
