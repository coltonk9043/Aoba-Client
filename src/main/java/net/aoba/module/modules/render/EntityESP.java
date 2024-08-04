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
 * EntityESP Module
 */
package net.aoba.module.modules.render;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render3D;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class EntityESP extends Module implements Render3DListener {
    private ColorSetting color_passive = new ColorSetting("entityesp_color_passive", "Passive Color", "Passive Color", new Color(0, 1f, 1f));
    private ColorSetting color_enemies = new ColorSetting("entityesp_color_enemy", "Enemy Color", "Enemy Color", new Color(0, 1f, 1f));
    private ColorSetting color_misc = new ColorSetting("entityesp_color_misc", "Misc. Color", "Misc. Color", new Color(0, 1f, 1f));
    private BooleanSetting showPassiveEntities = new BooleanSetting("entityesp_show_passive", "Show Passive Entities", "Show Passive Entities", true);
    private BooleanSetting showEnemies = new BooleanSetting("entityesp_show_enemies", "Show Enemies", "Show Enemies", true);
    private BooleanSetting showMiscEntities = new BooleanSetting("entityesp_show_misc", "Show Misc Entities", "Show Misc Entities", true);
    public BooleanSetting rainbow = new BooleanSetting("entityesp_rainbow", "Rainbow", "Rainbow", false);
    public FloatSetting effectSpeed = new FloatSetting("entityesp_effectspeed", "Effect Speed", "Effect Speed", 4f, 1f, 20f, 0.1f);
    private FloatSetting lineThickness = new FloatSetting("entityesp_linethickness", "Line Thickness", "Adjust the thickness of the ESP box lines", 2f, 0f, 5f, 0.1f);

    public EntityESP() {
        super(new KeybindSetting("key.entityesp", "EntityESP Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("EntityESP");
        this.setCategory(Category.of("Render"));
        this.setDescription("Allows the player to see entities with an ESP.");

        this.addSetting(color_passive);
        this.addSetting(color_enemies);
        this.addSetting(color_misc);
        this.addSetting(rainbow);
        this.addSetting(effectSpeed);
        this.addSetting(lineThickness);
        this.addSetting(showPassiveEntities);
        this.addSetting(showEnemies);
        this.addSetting(showMiscEntities);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void OnRender(Render3DEvent event) {
        MatrixStack matrixStack = event.GetMatrix();
        float partialTicks = event.GetPartialTicks();

        for (Entity entity : MC.world.getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                double interpolatedX = MathHelper.lerp(partialTicks, entity.prevX, entity.getX());
                double interpolatedY = MathHelper.lerp(partialTicks, entity.prevY, entity.getY());
                double interpolatedZ = MathHelper.lerp(partialTicks, entity.prevZ, entity.getZ());

                Box boundingBox = entity.getBoundingBox().offset(interpolatedX - entity.getX(), interpolatedY - entity.getY(), interpolatedZ - entity.getZ());

                Color color = getColorForEntity(entity);
                if (color != null) {
                	Render3D.drawEntityModel(matrixStack, partialTicks, entity, color, lineThickness.getValue());
                    //Render3D.draw3DBox(matrixStack, boundingBox, color, lineThickness.getValue());
                }
            }
        }
    }

    private Color getColorForEntity(Entity entity) {
        if (entity instanceof AnimalEntity && showPassiveEntities.getValue()) {
            return color_passive.getValue();
        } else if (entity instanceof Monster && showEnemies.getValue()) {
            return color_enemies.getValue();
        } else if (!(entity instanceof AnimalEntity || entity instanceof Monster) && showMiscEntities.getValue()) {
            return color_misc.getValue();
        }
        return null;
    }

}
