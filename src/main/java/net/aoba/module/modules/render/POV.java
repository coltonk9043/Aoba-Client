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
 * POV Module
 */
package net.aoba.module.modules.render;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.utils.entity.FakePlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class POV extends Module implements TickListener {
	private FakePlayerEntity fakePlayer;
	private String povString = null;
	private Entity povEntity = null;

	private boolean fakePlayerSpawned = false;

	public POV() {
		super("POV");
		this.setCategory(Category.of("Render"));
		this.setDescription("Allows the player to see someone else's point-of-view.");
	}

	@Override
	public void onDisable() {
		MinecraftClient.getInstance().setCameraEntity(MC.player);
		if (fakePlayer != null) {
			fakePlayer.despawn();
			MC.world.removeEntity(-3, null);
		}
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	public void setEntityPOV(String entity) {
		this.povString = entity;
	}

	public Entity getEntity() {
		return this.povEntity;
	}

	public PlayerEntity getEntityAsPlayer() {
		if (this.povEntity instanceof PlayerEntity) {
			return (PlayerEntity) this.povEntity;
		} else {
			return null;
		}
	}

	@Override
	public void onTick(Pre event) {
		ClientPlayerEntity player = MC.player;
		povEntity = null;
		for (Entity entity : MC.world.getPlayers()) {
			if (entity.getName().getString().equals(povString)) {
				povEntity = entity;
			}
		}
		if (MinecraftClient.getInstance().getCameraEntity() == povEntity) {
			if (!fakePlayerSpawned) {
				fakePlayer = new FakePlayerEntity();
				fakePlayer.copyFrom(player);
				fakePlayer.headYaw = player.headYaw;
				MC.world.addEntity(fakePlayer);
			}
			fakePlayer.copyFrom(player);
			fakePlayer.headYaw = player.headYaw;
		} else {
			if (fakePlayer != null) {
				fakePlayer.despawn();
				MC.world.removeEntity(-3, null);
			}

			if (povEntity == null) {
				MinecraftClient.getInstance().setCameraEntity(MC.player);
			} else {
				MinecraftClient.getInstance().setCameraEntity(povEntity);
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}