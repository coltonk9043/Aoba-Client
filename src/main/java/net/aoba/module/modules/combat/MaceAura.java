/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class MaceAura extends Module implements TickListener {
	private final FloatSetting radius = FloatSetting.builder().id("maceaura_radius").displayName("Radius")
			.description("Radius that MaceAura will trigger").defaultValue(5f).minValue(0.1f).maxValue(10f).step(0.1f)
			.build();

	private final FloatSetting height = FloatSetting.builder().id("maceaura_height").displayName("Height")
			.description("Determines how high MaceAura will jump. Higher distance = more damage.").defaultValue(100f)
			.minValue(1f).maxValue(255f).build();

	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("maceaura_target_animals")
			.displayName("Target Animals").description("Target animals.").defaultValue(false).build();

	private final BooleanSetting targetMonsters = BooleanSetting.builder().id("maceaura_target_monsters")
			.displayName("Target Monsters").description("Target Monsters.").defaultValue(true).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("maceaura_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("maceaura_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private LivingEntity entityToAttack;

	public MaceAura() {
		super("MaceAura");

		setCategory(Category.of("Combat"));
		setDescription(
				"Smashes players in your personal space with a Mace with extreme damage. Be sure to enable NoFall for best results.");

		addSetting(radius);
		addSetting(height);
		addSetting(targetAnimals);
		addSetting(targetMonsters);
		addSetting(targetPlayers);
		addSetting(targetFriends);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Negativity,
				AntiCheat.Karhu,
				AntiCheat.Buzz
		);
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
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		// if (MC.player.getMainHandStack().getItem() == Items.MACE &&
		// MC.player.getAttackCooldownProgress(0) == 1) {
		if (MC.player.getAttackCooldownProgress(0) == 1) {
			if (entityToAttack == null) {
				ArrayList<Entity> hitList = new ArrayList<Entity>();

				// Add all potential entities to the 'hitlist'
				if (targetAnimals.getValue() || targetMonsters.getValue()) {
					for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
						if (entity == MC.player)
							continue;
						if (MC.player.squaredDistanceTo(entity) > radius.getValueSqr())
							continue;

						if ((entity instanceof AnimalEntity && targetAnimals.getValue())
								|| (entity instanceof Monster && targetMonsters.getValue())) {
							hitList.add(entity);
						}
					}
				}

				// Add all potential players to the 'hitlist'
				if (targetPlayers.getValue()) {
					for (PlayerEntity player : MC.world.getPlayers()) {
						if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player))
							continue;

						if (player == MC.player || MC.player
								.squaredDistanceTo(player) > (radius.getValue() * radius.getValue())) {
							continue;
						}
						hitList.add(player);
					}
				}

				// For each entity, get the entity that matches a criteria.
				for (Entity entity : hitList) {
					LivingEntity le = (LivingEntity) entity;
					if (entityToAttack == null) {
						entityToAttack = le;
					} else {
						if (MC.player.squaredDistanceTo(le) <= MC.player.squaredDistanceTo(entityToAttack)) {
							entityToAttack = le;
						}
					}
				}

				if (entityToAttack != null) {
					// If the entity is found, we want to attach it.
					int packetsRequired = Math.round((float) Math.ceil(Math.abs(height.getValue() / 10.0f)));
					for (int i = 0; i < packetsRequired; i++) {
						MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false, false));
					}

					Vec3d newPos = MC.player.getPos().add(0, height.getValue(), 0);
					MC.player.networkHandler.sendPacket(
							new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, false, false));
				}
			} else {
				int packetsRequired = Math.round((float) Math.ceil(Math.abs(height.getValue() / 10.0f)));
				for (int i = 0; i < packetsRequired; i++) {
					MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false, false));
				}

				Vec3d newPos = MC.player.getPos();
				MC.player.networkHandler.sendPacket(
						new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, false, false));

				MC.interactionManager.attackEntity(MC.player, entityToAttack);
				MC.player.swingHand(Hand.MAIN_HAND);
				entityToAttack = null;
			}
		}
	}
}
