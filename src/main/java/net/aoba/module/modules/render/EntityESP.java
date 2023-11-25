/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.RenderEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.RenderListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.Color;
import net.aoba.misc.RainbowColor;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class EntityESP extends Module implements RenderListener, TickListener {
	private RainbowColor rainbowColor;

	private ColorSetting color_passive = new ColorSetting("entityesp_color_passive", "Passive Color",  "Passive Color", new Color(0, 1f, 1f));
	private ColorSetting color_enemies = new ColorSetting("entityesp_color_enemy", "Enemy Color", "Enemy Color", new Color(0, 1f, 1f));
	private ColorSetting color_misc = new ColorSetting("entityesp_color_misc", "Misc. Color", "Misc. Color", new Color(0, 1f, 1f));
	
	public BooleanSetting rainbow = new BooleanSetting("entityesp_rainbow", "Rainbow","Rainbow", false);
	public FloatSetting effectSpeed = new FloatSetting("entityesp_effectspeed", "Effect Speed", "Effect Speed", 4, 1, 20, 0.1);
	
	public EntityESP() {
		super(new KeybindSetting("key.entityesp", "EntityESP Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("EntityESP");
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see entities with an ESP.");
		rainbowColor = new RainbowColor();
		
		this.addSetting(color_passive);
		this.addSetting(color_enemies);
		this.addSetting(color_misc);
		
		this.addSetting(rainbow);
		this.addSetting(effectSpeed);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(RenderListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(RenderListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void OnRender(RenderEvent event) {
		MatrixStack matrixStack = event.GetMatrixStack();
		float partialTicks = event.GetPartialTicks();
		
		matrixStack.push();
		
		for (Entity entity : MC.world.getEntities()) {
			if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
				
				Box boundingBox = entity.getBoundingBox(); 
				
				Vec3d entityVelocity = entity.getVelocity();
				Vec3d velocityPartial = new Vec3d(entityVelocity.x * partialTicks, 0, entityVelocity.z * partialTicks);
				
				boundingBox = boundingBox.offset(velocityPartial);
				
				if (entity instanceof AnimalEntity) {
					this.getRenderUtils().draw3DBox(matrixStack, boundingBox, color_passive.getValue());
				} else if (entity instanceof Monster) {
					this.getRenderUtils().draw3DBox(matrixStack, boundingBox, color_enemies.getValue());
				} else {
					this.getRenderUtils().draw3DBox(matrixStack, boundingBox, color_misc.getValue());
				}
			}
		}
		matrixStack.pop();
	}

	@Override
	public void OnUpdate(TickEvent event) {
		/*
		 * if(this.rainbow.getValue()) {
		 * this.rainbowColor.update(this.effectSpeed.getValue().floatValue()); }else {
		 * 
		 * this.color.setHSV(hue.getValue().floatValue(), 1f, 1f); }
		 */
	}
}
