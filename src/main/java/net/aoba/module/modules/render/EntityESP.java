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

import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.DoubleSetting;
import net.aoba.gui.Color;
import net.aoba.misc.RainbowColor;
import net.aoba.module.Module;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class EntityESP extends Module {
	private Color color;
	private RainbowColor rainbowColor;

	public DoubleSetting hue = new DoubleSetting("entitysp_hue", "Hue", 4, 0, 360, 1);
	public BooleanSetting rainbow = new BooleanSetting("entityesp_rainbow", "Rainbow", false, null);
	public DoubleSetting effectSpeed = new DoubleSetting("entityesp_effectspeed", "Effect Speed", 4, 1, 20, 0.1);
	
	public EntityESP() {
		this.setName("EntityESP");
		this.setBind(new KeyBinding("key.entityesp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see entities with an ESP.");
		color = new Color(255, 0, 0);
		rainbowColor = new RainbowColor();
		
		this.addSetting(hue);
		this.addSetting(rainbow);
		this.addSetting(effectSpeed);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		if(this.rainbow.getValue()) {
			this.rainbowColor.update(this.effectSpeed.getValue().floatValue());
		}else {
			this.color.setHSV(hue.getValue().floatValue(), 1f, 1f);
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {	
		matrixStack.push();
		
		for (Entity entity : MC.world.getEntities()) {
			if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
				
				Box boundingBox = entity.getBoundingBox(); 
				
				Vec3d entityVelocity = entity.getVelocity();
				Vec3d velocityPartial = new Vec3d(entityVelocity.x * partialTicks, 0, entityVelocity.z * partialTicks);
				
				boundingBox = boundingBox.offset(velocityPartial);
				
				if (entity instanceof AnimalEntity) {
					this.getRenderUtils().draw3DBox(matrixStack, boundingBox, new Color(0, 255, 0), 0.2f);
				} else if (entity instanceof Monster) {
					this.getRenderUtils().draw3DBox(matrixStack, boundingBox, new Color(255, 0, 0), 0.2f);
				} else {
					this.getRenderUtils().draw3DBox(matrixStack, boundingBox, new Color(0, 0, 255), 0.2f);
				}
			}
		}
		matrixStack.pop();
	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}
