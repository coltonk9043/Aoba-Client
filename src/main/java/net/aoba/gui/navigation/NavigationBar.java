/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Color;
import net.aoba.gui.font.FontManager;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class NavigationBar implements MouseClickListener {
    MinecraftClient mc = MinecraftClient.getInstance();

    private List<Page> options;
    private int selectedIndex;
    private float currentSelectionX;
    private float targetSelectionX;
    private final float animationSpeed = 0.1f;

    public NavigationBar() {
        options = new ArrayList<>();
        Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
        currentSelectionX = 0;
        targetSelectionX = 0;
    }

    public void addPane(Page pane) {
        options.add(pane);
    }

    public List<Page> getPanes() {
        return this.options;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public Page getSelectedPage() {
        return options.get(selectedIndex);
    }

    public void setSelectedIndex(int index) {
        if (index < this.options.size()) {
            this.options.get(selectedIndex).setVisible(false);
            this.selectedIndex = index;
            this.options.get(selectedIndex).setVisible(true);
            targetSelectionX = index * 100; // Update target position for animation
        }
    }

    public void update() {
        if (options.size() > 0) {
            options.get(selectedIndex).update();
        }
    }

    public void draw(DrawContext drawContext, float partialTicks) {
        Window window = mc.getWindow();
        int centerX = (window.getWidth() / 2);
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        int width = 100 * options.size();

        // Animate selection box movement
        currentSelectionX += (targetSelectionX - currentSelectionX) * animationSpeed * partialTicks;

        Render2D.drawRoundedBox(matrix, centerX - ((float) width / 2), 25, width, 25, GuiManager.roundingRadius.getValue(), GuiManager.backgroundColor.getValue());
        Render2D.drawRoundedBoxOutline(matrix, centerX - ((float) width / 2), 25, width, 25, GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue());

        // Use currentSelectionX for animated position
        Render2D.drawRoundedBox(matrix, centerX - ((float) width / 2) + currentSelectionX, 25, 100, 25, GuiManager.roundingRadius.getValue() - 1, new Color(150, 150, 150, 100));

        for (int i = 0; i < options.size(); i++) {
            Page pane = options.get(i);
            if (i == selectedIndex) {
                pane.render(drawContext, partialTicks);
            }
            Render2D.drawString(drawContext, pane.title, centerX - ((float) width / 2) + 50 + (100 * i) - Render2D.getStringWidth(pane.title), 30, GuiManager.foregroundColor.getValue());
        }
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            AobaClient aoba = Aoba.getInstance();
            Window window = mc.getWindow();

            double mouseX = event.mouseX;
            double mouseY = event.mouseY;
            int width = 100 * options.size();
            int centerX = (window.getWidth() / 2);
            int x = centerX - (width / 2);

            if (aoba.guiManager.isClickGuiOpen()) {
                if (mouseX >= (x) && mouseX <= (x + width)) {
                    if (mouseY >= (25) && mouseY <= (50)) {
                        int mouseXInt = (int) mouseX;
                        int selection = (mouseXInt - x) / 100;
                        this.setSelectedIndex(selection);
                        event.cancel();
                    }
                }
            }
        }
    }
}