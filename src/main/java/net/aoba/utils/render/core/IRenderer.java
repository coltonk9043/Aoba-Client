package net.aoba.utils.render.core;

import net.aoba.gui.colors.Color;
import net.minecraft.client.gui.DrawContext;

public interface IRenderer {
    void begin();
    void end();
    boolean isBuilding();
    void reset();
    void render(DrawContext context);
}