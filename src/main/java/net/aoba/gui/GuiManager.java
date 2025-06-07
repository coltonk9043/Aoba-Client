/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.Render2DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.event.listeners.Render2DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.RainbowColor;
import net.aoba.gui.colors.RandomColor;
import net.aoba.gui.components.GridComponent;
import net.aoba.gui.components.ImageComponent;
import net.aoba.gui.components.ModuleComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.navigation.NavigationBar;
import net.aoba.gui.navigation.Page;
import net.aoba.gui.navigation.Window;
import net.aoba.gui.navigation.huds.ArmorHud;
import net.aoba.gui.navigation.huds.CoordsHud;
import net.aoba.gui.navigation.huds.DayHud;
import net.aoba.gui.navigation.huds.FPSHud;
import net.aoba.gui.navigation.huds.ModuleArrayListHud;
import net.aoba.gui.navigation.huds.ModuleSelectorHud;
import net.aoba.gui.navigation.huds.NetherCoordsHud;
import net.aoba.gui.navigation.huds.PingHud;
import net.aoba.gui.navigation.huds.RadarHud;
import net.aoba.gui.navigation.huds.SpeedHud;
import net.aoba.gui.navigation.huds.TimeHud;
import net.aoba.gui.navigation.huds.WatermarkHud;
import net.aoba.gui.navigation.windows.AntiCheatWindow;
import net.aoba.gui.navigation.windows.AuthCrackerWindow;
import net.aoba.gui.navigation.windows.GoToWindow;
import net.aoba.gui.navigation.windows.HudOptionsWindow;
import net.aoba.gui.navigation.windows.MacroWindow;
import net.aoba.gui.navigation.windows.SettingsWindow;
import net.aoba.gui.navigation.windows.ToggleHudsTab;
import net.aoba.managers.SettingManager;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.input.Input;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;

public class GuiManager implements KeyDownListener, TickListener, Render2DListener {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private static CursorStyle currentCursor = CursorStyle.Default;
	private static String tooltip = null;

	public KeybindSetting clickGuiButton = KeybindSetting.builder().id("key.clickgui").displayName("ClickGUI Key")
			.defaultValue(InputUtil.fromKeyCode(GLFW.GLFW_KEY_RIGHT_SHIFT, 0)).build();

	private final KeyBinding esc = new KeyBinding("key.esc", GLFW.GLFW_KEY_ESCAPE, "key.categories.aoba");

	private boolean clickGuiOpen = false;
	private final HashMap<Object, Window> pinnedHuds = new HashMap<Object, Window>();

	// Navigation Bar and Pages
	public NavigationBar clickGuiNavBar;
	public Page modulesPane = new Page("Modules");
	public Page toolsPane = new Page("Tools");
	public Page hudPane = new Page("Hud");

	// Global HUD Settings
	public static BooleanSetting enableCustomTitle = BooleanSetting.builder().id("enable_custom_title")
			.displayName("Enable Custom Title Screen").defaultValue(true).build();

	public static BooleanSetting enableTooltips = BooleanSetting.builder().id("enable_tooltips")
			.displayName("Enable Tooltips").defaultValue(true).build();

	public static ColorSetting foregroundColor = ColorSetting.builder().id("hud_foreground_color")
			.displayName("GUI Foreground Color").description("Color of the foreground.")
			.defaultValue(new Color(238, 21, 247)).build();

	public static ColorSetting borderColor = ColorSetting.builder().id("hud_border_color")
			.displayName("GUI Border Color").description("Color of the borders.").defaultValue(new Color(0, 0, 0))
			.build();

	public static ColorSetting backgroundColor = ColorSetting.builder().id("hud_background_color")
			.displayName("GUI Background Color").description("Color of the background.")
			.defaultValue(new Color(0, 0, 0, 50)).build();

	public static FloatSetting roundingRadius = FloatSetting.builder().id("hud_rounding_radius")
			.displayName("Corner Rounding").description("The radius of the rounding on hud.").defaultValue(6f)
			.minValue(0f).maxValue(10f).step(1f).build();

	public static FloatSetting dragSmoothening = FloatSetting.builder().id("gui_drag_smoothening")
			.displayName("Drag Smooth Speed").description("The value for the dragging smoothening").defaultValue(1.0f)
			.minValue(0.1f).maxValue(2.0f).step(0.1f).build();

	public static RainbowColor rainbowColor = new RainbowColor();
	public static RandomColor randomColor = new RandomColor();

	public ModuleSelectorHud moduleSelector;
	public ArmorHud armorHud;
	public RadarHud radarHud;
	public TimeHud timeHud;
	public DayHud dayHud;
	public ModuleArrayListHud moduleArrayListHud;
	public WatermarkHud watermarkHud;
	public CoordsHud coordsHud;
	public NetherCoordsHud netherCoordsHud;
	public FPSHud fpsHud;
	public PingHud pingHud;
	public SpeedHud speedHud;

	public GuiManager() {
		clickGuiNavBar = new NavigationBar();

		net.minecraft.client.util.Window window = MC.getWindow();

		SettingManager.registerGlobalSetting(borderColor);
		SettingManager.registerGlobalSetting(backgroundColor);
		SettingManager.registerGlobalSetting(foregroundColor);
		SettingManager.registerGlobalSetting(roundingRadius);

		SettingManager.registerSetting(clickGuiButton);
		Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(Render2DListener.class, this);
	}

	public void Initialize() {
		System.out.println("Initializing");
		toolsPane.addWindow(new AuthCrackerWindow());
		toolsPane.addWindow(new GoToWindow());
		toolsPane.addWindow(new MacroWindow());

		moduleSelector = new ModuleSelectorHud();
		armorHud = new ArmorHud(0, 0);
		radarHud = new RadarHud(0, 0);
		timeHud = new TimeHud(0, 0);
		dayHud = new DayHud(0, 0);
		moduleArrayListHud = new ModuleArrayListHud(0, 0);
		watermarkHud = new WatermarkHud(0, 0);
		coordsHud = new CoordsHud(0, 0);
		netherCoordsHud = new NetherCoordsHud(0, 0);
		fpsHud = new FPSHud(0, 0);
		pingHud = new PingHud(0, 0);
		speedHud = new SpeedHud(0, 0);

		ArrayList<HudWindow> huds = Lists.newArrayList(moduleSelector, armorHud, radarHud, timeHud, dayHud,
				moduleArrayListHud, watermarkHud, coordsHud, netherCoordsHud, fpsHud, pingHud, speedHud);
		hudPane.addWindow(new HudOptionsWindow());
		hudPane.addWindow(new ToggleHudsTab(huds));
		Map<String, Category> categories = Category.getAllCategories();
		float xOffset = 50;

		for (Category category : categories.values()) {
			Window tab = new Window(category.getName(), xOffset, 75.0f);
			StackPanelComponent stackPanel = new StackPanelComponent();
			stackPanel.setMargin(new Margin(null, 30f, null, null));

			GridComponent gridComponent = new GridComponent();
			gridComponent.addColumnDefinition(new GridDefinition(30, RelativeUnit.Absolute)); // Fill 30px
			gridComponent.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative)); // Fill all remaining space

			ImageComponent img = new ImageComponent(category.getIcon());
			img.setMargin(new Margin(4f, 0f, 4f, 0f));
			gridComponent.addChild(img);

			StringComponent title = new StringComponent(category.getName());
			title.setIsHitTestVisible(false);
			gridComponent.addChild(title);

			stackPanel.addChild(gridComponent);

			SeparatorComponent separator = new SeparatorComponent();
			separator.setIsHitTestVisible(false);
			stackPanel.addChild(separator);

			// Loop through modules and add them to the correct category
			for (Module module : Aoba.getInstance().moduleManager.modules) {
				if (module.getCategory().equals(category)) {
					ModuleComponent button = new ModuleComponent(module);
					stackPanel.addChild(button);
				}
			}

			tab.addChild(stackPanel);
			tab.setMaxWidth(600f);
			modulesPane.addWindow(tab);

			xOffset += tab.getMinWidth() + 10;
		}

		modulesPane.addWindow(new SettingsWindow());
		modulesPane.addWindow(new AntiCheatWindow());

		clickGuiNavBar.addPane(modulesPane);
		clickGuiNavBar.addPane(toolsPane);
		clickGuiNavBar.addPane(hudPane);

		modulesPane.initialize();
		toolsPane.initialize();
		hudPane.initialize();

		clickGuiNavBar.setSelectedIndex(0);
	}

	public static CursorStyle getCursor() {
		return currentCursor;
	}

	public static void setCursor(CursorStyle cursor) {
		currentCursor = cursor;
		Input.setCursorStyle(currentCursor);
	}

	public static String getTooltip() {
		return tooltip;
	}

	public static void setTooltip(String tt) {
		if (tooltip != tt)
			tooltip = tt;
	}

	public void addWindow(Window hud, String pageName) {
		for (Page page : clickGuiNavBar.getPanes()) {
			if (page.getTitle().equals(pageName)) {
				page.addWindow(hud);
				page.moveToFront(hud);
				hud.initialize();
				break;
			}
		}
	}

	public void removeWindow(Window hud, String pageName) {
		for (Page page : clickGuiNavBar.getPanes()) {
			if (page.getTitle().equals(pageName)) {
				page.removeWindow(hud);
				break;
			}
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (clickGuiButton.getValue().getCode() == event.GetKey() && MC.currentScreen == null) {
			setClickGuiOpen(!clickGuiOpen);
			toggleMouse();
		}
	}

	public void setHudActive(HudWindow hud, boolean state) {
		if (state) {
			pinnedHuds.put(hud.getClass(), hud);
			hud.activated.silentSetValue(true);
			hudPane.addWindow(hud);
		} else {
			pinnedHuds.remove(hud.getClass());
			hud.activated.silentSetValue(false);
			hudPane.removeWindow(hud);
		}
	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		/**
		 * Moves the selected Tab to where the user moves their mouse.
		 */
		if (clickGuiOpen) {
			clickGuiNavBar.update();
		}

		/**
		 * Updates each of the Tab GUIs that are currently on the screen.
		 */
		for (Window hud : pinnedHuds.values()) {
			hud.update();
		}

		if (esc.isPressed() && clickGuiOpen) {
			clickGuiOpen = false;
			toggleMouse();
		}
	}

	@Override
	public void onRender(Render2DEvent event) {
		DrawContext drawContext = event.getDrawContext();
		float tickDelta = event.getRenderTickCounter().getTickProgress(false);

		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();

		int guiScale = MC.getWindow().calculateScaleFactor(MC.options.getGuiScale().getValue(), MC.forcesUnicodeFont());
		matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

		net.minecraft.client.util.Window window = MC.getWindow();

		// Render ClickGUI and Topbar
		if (clickGuiOpen) {
			Render2D.drawBox(drawContext, 0, 0, window.getWidth(), window.getHeight(), new Color(26, 26, 26, 100));
			clickGuiNavBar.draw(drawContext, tickDelta);
		}

		// Render HUDS
		if (!clickGuiOpen) {
			for (Window hud : pinnedHuds.values()) {
				hud.draw(drawContext, tickDelta);
			}
		}

		// Draw Tooltip on top of all UI elements
		if (tooltip != null && enableTooltips.getValue()) {
			int mouseX = (int) MC.mouse.getX();
			int mouseY = (int) MC.mouse.getY();
			int tooltipWidth = Render2D.getStringWidth(tooltip) + 2;
			int tooltipHeight = 10;

			Render2D.drawRoundedBox(drawContext, mouseX + 12, mouseY + 12, (tooltipWidth + 4) * 2,
					(tooltipHeight + 4) * 2, roundingRadius.getValue(), backgroundColor.getValue().getAsSolid());
			Render2D.drawString(drawContext, tooltip, mouseX + 18, mouseY + 18, foregroundColor.getValue());
		}
		matrixStack.pop();
	}

	/**
	 * Gets whether or not the Click GUI is currently open.
	 *
	 * @return State of the Click GUI.
	 */
	public boolean isClickGuiOpen() {
		return clickGuiOpen;
	}

	public void setClickGuiOpen(boolean state) {
		clickGuiOpen = state;
		setTooltip(null);
	}

	/**
	 * Locks and unlocks the Mouse.
	 */
	public void toggleMouse() {
		if (MC.mouse.isCursorLocked()) {
			MC.mouse.unlockCursor();
		} else {
			MC.mouse.lockCursor();
		}
	}
}
