package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.GuiManager;
import net.aoba.misc.Render2D;
import net.aoba.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import static net.aoba.AobaClient.MC;

public class ModuleArrayListHud extends AbstractHud {
    public ModuleArrayListHud(int x, int y) {
        super("ModuleArrayListHud", x, y, 190, 200);
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        Window window = MC.getWindow();

        // Draws the active mods in the top right of the screen.
        AobaClient aoba = Aoba.getInstance();
        int iteration = 0;
        for (int i = 0; i < aoba.moduleManager.modules.size(); i++) {
            Module mod = aoba.moduleManager.modules.get(i);
            if (mod.getState()) {
                Render2D.drawString(drawContext, mod.getName(),
                        (float) (window.getWidth() - ((MC.textRenderer.getWidth(mod.getName()) + 5) * 2)),
                        10 + (iteration * 20), GuiManager.foregroundColor.getValue().getColorAsInt());
                iteration++;
            }
        }
    }
}
