package net.aoba.gui.navigation.huds;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.misc.Render2D;
import net.aoba.module.Module;
import net.minecraft.client.gui.DrawContext;
import java.util.Comparator;

public class ModuleArrayListHud extends HudWindow {
    public ModuleArrayListHud(int x, int y) {
        super("ModuleArrayListHud", x, y, 0, 0);
        resizeable = false;
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        if (this.visible) {
            Rectangle pos = position.getValue();

            if (pos.isDrawable()) {
                AobaClient aoba = Aoba.getInstance();
                final int[] iteration = {0};

                int maxWidth = 0;
                int totalHeight = 0;

                for (Module mod : aoba.moduleManager.modules) {
                    if (mod.getState()) {
                        int textWidth = MC.textRenderer.getWidth(mod.getName());
                        maxWidth = Math.max(maxWidth, textWidth);
                        totalHeight += 20;
                    }
                }

                this.setWidth(maxWidth * 2 + 10);
                this.setHeight(totalHeight + 10);

                aoba.moduleManager.modules.stream()
                        .filter(Module::getState)
                        .sorted(Comparator.comparingInt(mod -> -MC.textRenderer.getWidth(mod.getName())))
                        .forEachOrdered(mod -> {
                            int textWidth = MC.textRenderer.getWidth(mod.getName());
                            int centeredX = (int) (pos.getX().intValue() + (pos.getWidth() - textWidth) / 2) - 18;
                            int yPosition = pos.getY().intValue() + 10 + (iteration[0] * 20);

                            Render2D.drawString(drawContext, mod.getName(),
                                    (float) centeredX,
                                    (float) yPosition, GuiManager.foregroundColor.getValue().getColorAsInt());
                            iteration[0]++;
                        });
            }
        }
    }
}
