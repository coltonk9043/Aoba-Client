/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.SubtickEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.SubtickListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.player.InteractionUtils;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MaceAura extends Module implements SubtickListener, TickListener {
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

	private final BooleanSetting triggerOnClick = BooleanSetting.builder().id("maceaura_trigger_on_click")
			.displayName("Trigger On Click").description("Only attack while the attack key is held down.")
			.defaultValue(false).build();

	private LivingEntity selectedTarget;
	private boolean jumped;

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
		addSetting(triggerOnClick);

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
		Aoba.getInstance().eventManager.RemoveListener(SubtickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		selectedTarget = null;
		jumped = false;
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(SubtickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSubtick(SubtickEvent event) {
		float partialTick = MC.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		Vec3 playerPos = MC.player.getPosition(partialTick);
		float radiusSqr = radius.getValueSqr();

		LivingEntity best = null;
		double bestDistSqr = 0;

		if (targetAnimals.getValue() || targetMonsters.getValue()) {
			for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
				if (entity == MC.player)
					continue;
				if (!((entity instanceof Animal && targetAnimals.getValue())
						|| (entity instanceof Enemy && targetMonsters.getValue())))
					continue;

				double distSqr = playerPos.distanceToSqr(entity.getPosition(partialTick));
				if (distSqr > radiusSqr)
					continue;

				if (best == null || distSqr < bestDistSqr) {
					best = (LivingEntity) entity;
					bestDistSqr = distSqr;
				}
			}
		}

		if (targetPlayers.getValue()) {
			for (Player player : MC.level.players()) {
				if (player == MC.player)
					continue;
				if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player))
					continue;

				double distSqr = playerPos.distanceToSqr(player.getPosition(partialTick));
				if (distSqr > radiusSqr)
					continue;

				if (best == null || distSqr < bestDistSqr) {
					best = player;
					bestDistSqr = distSqr;
				}
			}
		}

		selectedTarget = best;
	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		// if (MC.player.getMainHandStack().getItem() == Items.MACE &&
		// MC.player.getAttackCooldownProgress(0) == 1) {
		if (MC.player.getAttackStrengthScale(0) != 1)
			return;

		if (!jumped) {
			if (triggerOnClick.getValue() && !MC.options.keyAttack.isDown())
				return;

			LivingEntity target = selectedTarget;
			if (target == null || target.isRemoved() || !target.isAlive())
				return;

			int packetsRequired = Math.round((float) Math.ceil(Math.abs(height.getValue() / 10.0f)));
			for (int i = 0; i < packetsRequired; i++) {
				MC.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(false, false));
			}

			Vec3 newPos = MC.player.position().add(0, height.getValue(), 0);
			MC.player.connection.send(new ServerboundMovePlayerPacket.Pos(newPos.x, newPos.y, newPos.z, false, false));
			jumped = true;
		} else {
			int packetsRequired = Math.round((float) Math.ceil(Math.abs(height.getValue() / 10.0f)));
			for (int i = 0; i < packetsRequired; i++) {
				MC.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(false, false));
			}

			Vec3 newPos = MC.player.position();
			MC.player.connection.send(new ServerboundMovePlayerPacket.Pos(newPos.x, newPos.y, newPos.z, false, false));

			LivingEntity target = selectedTarget;
			if (target != null && !target.isRemoved() && target.isAlive())
				InteractionUtils.attack(target);
			jumped = false;
		}
	}
}
