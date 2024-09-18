/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.Render2DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.event.listeners.Render2DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.RainbowColor;
import net.aoba.gui.colors.RandomColor;
import net.aoba.gui.components.ModuleComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.navigation.NavigationBar;
import net.aoba.gui.navigation.Page;
import net.aoba.gui.navigation.PinnableWindow;
import net.aoba.gui.navigation.Window;
import net.aoba.gui.navigation.huds.*;
import net.aoba.gui.navigation.windows.AuthCrackerWindow;
import net.aoba.gui.navigation.windows.GoToWindow;
import net.aoba.gui.navigation.windows.HudOptionsTab;
import net.aoba.gui.navigation.windows.ToggleHudsTab;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.SettingManager;
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
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuiManager implements KeyDownListener, TickListener, Render2DListener {
    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static CursorStyle currentCursor = CursorStyle.Default;
    private static String tooltip = null;
    
    public KeybindSetting clickGuiButton = new KeybindSetting("key.clickgui", "ClickGUI Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_GRAVE_ACCENT, 0));
    private final KeyBinding esc = new KeyBinding("key.esc", GLFW.GLFW_KEY_ESCAPE, "key.categories.aoba");

    private boolean clickGuiOpen = false;
    private final HashMap<Object, Window> pinnedHuds = new HashMap<Object, Window>();

    // Navigation Bar and Pages
    public NavigationBar clickGuiNavBar;
    public Page modulesPane = new Page("Modules");
    public Page toolsPane = new Page("Tools");
    public Page hudPane = new Page("Hud");

    // Global HUD Settings
    public static ColorSetting foregroundColor;
    public static ColorSetting borderColor;
    public static ColorSetting backgroundColor;
    public static FloatSetting roundingRadius;
    public static FloatSetting dragSmoothening;

    public static RainbowColor rainbowColor = new RainbowColor();
    public static RandomColor randomColor = new RandomColor();

    public FloatSetting effectSpeed = new FloatSetting("color_speed", "Effect Spd", 4f, 1f, 20f, 0.1f, null);
    public BooleanSetting rainbow = new BooleanSetting("rainbow_mode", "Rainbow", false, null);
    public BooleanSetting ah = new BooleanSetting("armorhud_toggle", "ArmorHUD", false, null);

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
        borderColor = new ColorSetting("hud_border_color", "Color of the borders.", new Color(0, 0, 0));
        backgroundColor = new ColorSetting("hud_background_color", "Color of the background.", new Color(0, 0, 0, 50));
        foregroundColor = new ColorSetting("hud_foreground_color", "The color of the HUD", new Color(1.0f, 1.0f, 1.0f));
        roundingRadius = new FloatSetting("hud_rounding_radius", "The radius of the rounding on hud.", 6f, 0f, 10f, 1f);
        dragSmoothening = new FloatSetting("gui_drag_smoothening", "The value for the dragging smoothening", 1f, 0.1f, 2f, 0.1f);
        clickGuiNavBar = new NavigationBar();

        SettingManager.registerSetting(borderColor, Aoba.getInstance().settingManager.configContainer);
        SettingManager.registerSetting(backgroundColor, Aoba.getInstance().settingManager.configContainer);
        SettingManager.registerSetting(foregroundColor, Aoba.getInstance().settingManager.configContainer);
        SettingManager.registerSetting(clickGuiButton, Aoba.getInstance().settingManager.modulesContainer);
        SettingManager.registerSetting(roundingRadius, Aoba.getInstance().settingManager.configContainer);

        Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
        Aoba.getInstance().eventManager.AddListener(Render2DListener.class, this);
    }
    
    public void Initialize() {
        toolsPane.AddWindow(new AuthCrackerWindow());
        toolsPane.AddWindow(new GoToWindow("Go To Location", 1220, 550));
        
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

    
        ArrayList<HudWindow> huds = Lists.newArrayList(moduleSelector, armorHud, radarHud, timeHud, dayHud, moduleArrayListHud, watermarkHud, coordsHud, netherCoordsHud, fpsHud, pingHud, speedHud);
        hudPane.AddWindow(new HudOptionsTab());
        hudPane.AddWindow(new ToggleHudsTab(huds));
        
        Map<String, Category> categories = Category.getAllCategories();
        float xOffset = 50;

        for (Category category : categories.values()) {
            PinnableWindow tab = new PinnableWindow(category.getName(), xOffset, 75.0f, 180f, 180f);
            tab.setInheritHeightFromChildren(true);
            StackPanelComponent stackPanel = new StackPanelComponent(tab);
            stackPanel.setMargin(new Margin(null, 30f, null, null));

            // Loop through modules and add them to the correct category
            for (Module module : Aoba.getInstance().moduleManager.modules) {
                if (module.getCategory().equals(category)) {
                    ModuleComponent button = new ModuleComponent(module.getName(), stackPanel, module);
                    stackPanel.addChild(button);
                }
            }

            tab.addChild(stackPanel);
            tab.setWidth(180);
            modulesPane.AddWindow(tab);
            xOffset += tab.getActualSize().getWidth() + 10;
        }

        clickGuiNavBar.addPane(modulesPane);
        clickGuiNavBar.addPane(toolsPane);
        clickGuiNavBar.addPane(hudPane);
        // clickGuiNavBar.addPane(settingsPane);

        SettingManager.registerSetting(effectSpeed, Aoba.getInstance().settingManager.configContainer);
        SettingManager.registerSetting(rainbow, Aoba.getInstance().settingManager.configContainer);
        SettingManager.registerSetting(ah, Aoba.getInstance().settingManager.configContainer);

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
    	if(tooltip != tt)
    		tooltip = tt;
    }
    
    public void AddWindow(Window hud, String pageName) {
        for (Page page : clickGuiNavBar.getPanes()) {
            if (page.getTitle().equals(pageName)) {
                page.AddWindow(hud);
                page.moveToFront(hud);
                break;
            }
        }
    }

    public void RemoveWindow(Window hud, String pageName) {
        for (Page page : clickGuiNavBar.getPanes()) {
            if (page.getTitle().equals(pageName)) {
                page.RemoveWindow(hud);
                break;
            }
        }
    }

    @Override
    public void OnKeyDown(KeyDownEvent event) {
        if (clickGuiButton.getValue().getCode() == event.GetKey() && MC.currentScreen == null) {
        	setClickGuiOpen(!this.clickGuiOpen);
            this.toggleMouse();
        }
    }

    public void SetHudActive(HudWindow hud, boolean state) {
        if (state) {
            pinnedHuds.put(hud.getClass(), hud);
            hudPane.AddWindow(hud);
            hud.activated.silentSetValue(true);
        } else {
            this.pinnedHuds.remove(hud.getClass());
            hudPane.RemoveWindow(hud);
            hud.activated.silentSetValue(false);
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
        if (this.clickGuiOpen) {
            clickGuiNavBar.update();
        }

        /**
         * Updates each of the Tab GUIs that are currently on the screen.
         */
        for (Window hud : pinnedHuds.values()) {
            hud.update();
        }

        if (this.esc.isPressed() && this.clickGuiOpen) {
            this.clickGuiOpen = false;
            this.toggleMouse();
        }
    }

    @Override
    public void OnRender(Render2DEvent event) {

    	RenderSystem.disableCull();
    	RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        DrawContext drawContext = event.getDrawContext();
        float tickDelta = event.getRenderTickCounter().getTickDelta(false);

        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();

        int guiScale = MC.getWindow().calculateScaleFactor(MC.options.getGuiScale().getValue(), MC.forcesUnicodeFont());
        matrixStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        net.minecraft.client.util.Window window = (net.minecraft.client.util.Window) MC.getWindow();
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        /**
         * Render ClickGUI and Sidebar
         */
        if (this.clickGuiOpen) {
            Render2D.drawBox(matrix, 0, 0, window.getWidth(), window.getHeight(), new Color(26, 26, 26, 100));
            clickGuiNavBar.draw(drawContext, tickDelta);
        }

        // Render HUDS
        if (!this.clickGuiOpen) {
            for (Window hud : pinnedHuds.values()) {
                hud.draw(drawContext, tickDelta);
            }
        }

        // Draw Tooltip on top of all UI elements
        if(tooltip != null) {
        	int mouseX = (int) MC.mouse.getX();
            int mouseY = (int) MC.mouse.getY();
            int tooltipWidth = Render2D.getStringWidth(tooltip) + 2;
            int tooltipHeight = 10;

            Render2D.drawRoundedBox(matrixStack.peek().getPositionMatrix(), mouseX + 12, mouseY + 12, (tooltipWidth + 4) * 2, (tooltipHeight + 4) * 2, GuiManager.roundingRadius.getValue(), GuiManager.backgroundColor.getValue().getAsSolid());
            Render2D.drawString(drawContext, tooltip, mouseX + 18, mouseY + 18, GuiManager.foregroundColor.getValue());
        }
        
        
        matrixStack.pop();
        RenderSystem.enableCull();
    }

    /**
     * Gets whether or not the Click GUI is currently open.
     *
     * @return State of the Click GUI.
     */
    public boolean isClickGuiOpen() {
        return this.clickGuiOpen;
    }

    public void setClickGuiOpen(boolean state) {
        this.clickGuiOpen = state;
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
