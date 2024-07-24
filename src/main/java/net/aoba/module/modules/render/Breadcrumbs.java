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

/**
 * Breadcrumbs Module
 */
package net.aoba.module.modules.render;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.misc.Render3D;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import java.util.LinkedList;

public class Breadcrumbs extends Module implements Render3DListener, TickListener {
    private ColorSetting color = new ColorSetting("breadcrumbs_color", "Color", "Color", new Color(0, 1f, 1f));
    public FloatSetting lineThickness = new FloatSetting("breadcrumbs_linethickness", "Line Thickness", "Line Thickness", 1f, 0.1f, 10f, 0.1f);

    private final float distanceThreshold = 1.0f; // Minimum distance to record a new position
    private float currentTick = 0;
    private float timer = 10;
    private final LinkedList<Vec3d> positions = new LinkedList<>();
    private final int maxPositions = 1000;


    public Breadcrumbs() {
        super(new KeybindSetting("key.breadcrumbs", "Breadcrumbs Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
        this.setName("Breadcrumbs");
        this.setCategory(Category.of("Render"));
        this.setDescription("Shows breadcrumbs of where you last stepped;");
        this.addSetting(color);
        this.addSetting(lineThickness);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
        positions.clear();
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void OnRender(Render3DEvent event) {
        Vec3d prevPosition = null;
        for (Vec3d position : positions) {
            if (prevPosition != null) {
                Render3D.drawLine3D(event.GetMatrix(), prevPosition, position, color.getValue(), lineThickness.getValue().floatValue());
            }
            prevPosition = position;
        }
    }

    @Override
    public void OnUpdate(TickEvent event) {
        currentTick++;
        if (timer == currentTick) {
            currentTick = 0;
            if (!Aoba.getInstance().moduleManager.freecam.getState()) {
                Vec3d currentPosition = MC.player.getPos();
                if (positions.isEmpty() || positions.getLast().squaredDistanceTo(currentPosition) >= distanceThreshold * distanceThreshold) {
                    if (positions.size() >= maxPositions) {
                        positions.removeFirst();
                    }
                    positions.add(currentPosition);
                }
            }
        }
    }
}