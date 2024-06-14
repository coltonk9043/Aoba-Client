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
 * Crystal Aura Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CrystalAura extends Module implements TickListener {
	
	private FloatSetting radius;
	
	public CrystalAura() {
		super(new KeybindSetting("key.crystalaura", "Crystal Aura Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("CrystalAura");
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything within your personal space.");
		
		radius = new FloatSetting("crystalaura_radius", "Radius", "Radius, in blocks, that you can place/attack a crystal.", 5f, 1f, 15f, 1f);
		this.addSetting(radius);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void OnUpdate(TickEvent event) {
		for (PlayerEntity player : MC.world.getPlayers()) {
			if (player == MC.player || MC.player.distanceTo(player) > this.radius.getValue()) {
				continue;
			}
			// Get the block position below the player.
			BlockPos entityPos = player.getBlockPos();
			BlockPos check = entityPos.add(0, -1, 0);
			BlockState bs = MC.world.getBlockState(check);
			Block block = bs.getBlock();
			if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK)
				continue;

			for (int slot = 0; slot < 9; slot++) {
				Item item = MC.player.getInventory().getStack(slot).getItem();
				if (item == Items.END_CRYSTAL) {
					MC.player.getInventory().selectedSlot = slot;
					break;
				}
			}
			BlockHitResult rayTrace = new BlockHitResult(check.toCenterPos(), Direction.UP, check, false);
			MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, rayTrace);
			this.MC.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, MC.player.getYaw(), MC.player.getPitch()));
		}

		// Hit the entity that has been found.
		Entity entity = null;
		for (Entity ent : MC.world.getEntities()) {
			if (MC.player.distanceTo(ent) < radius.getValue()) {
				if (ent instanceof EndCrystalEntity) {
					entity = ent;
					break;
				}
			}
		}
	
		if(entity != null) {
			MC.player.swingHand(Hand.MAIN_HAND);
			MC.interactionManager.attackEntity(MC.player, entity);
			MC.player.attack(entity);
		}
	}
}