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
 * Aimbot Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;

public class Aimbot extends Module {

	private LivingEntity temp = null;

	private BooleanSetting targetAnimals;
	private BooleanSetting targetPlayers;

	public Aimbot() {
		this.setName("Aimbot");
		this.setBind(new KeyBinding("key.aimbot", GLFW.GLFW_KEY_K, "key.categories.aoba"));

		this.setCategory(Category.Combat);
		this.setDescription("Locks your crosshair towards a desire player or entity.");
		targetAnimals = new BooleanSetting("Trgt Mobs", "aimbot_target_mobs");
		targetPlayers = new BooleanSetting("Trgt Players", "aimbot_target_players");
		this.addSetting(targetAnimals);
		this.addSetting(targetPlayers);
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

		if (this.targetPlayers.getValue()) {
			if (MC.world.getPlayers().size() == 2) {
				temp = MC.world.getPlayers().get(1);
			} else if (MC.world.getPlayers().size() > 2) {
				for (int x = 0; x < MC.world.getPlayers().size(); x++) {
					for (int y = 1; y < MC.world.getPlayers().size(); y++) {
						if (MC.world.getPlayers().get(x).distanceTo(MC.player) < MC.world.getPlayers().get(y)
								.distanceTo(MC.player)) {
							temp = MC.world.getPlayers().get(x);
						}
					}
				}
			}
		}
		if (this.targetAnimals.getValue()) {
			LivingEntity tempEntity = null;
			for (Entity entity : MC.world.getEntities()) {
				if (!(entity instanceof LivingEntity))
					continue;
				if (entity instanceof ClientPlayerEntity)
					continue;
				if (tempEntity == null) {
					tempEntity = (LivingEntity) entity;
				} else {
					if (entity.distanceTo(MC.player) < tempEntity.distanceTo(MC.player)) {
						tempEntity = (LivingEntity) entity;
					}
				}
			}
			temp = tempEntity;
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		if (temp != null) {
			MC.player.lookAt(EntityAnchor.EYES, temp.getEyePos());
		}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}