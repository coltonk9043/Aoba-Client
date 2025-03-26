/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.EntityGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoShear extends Module implements TickListener {

	private final FloatSetting radius = FloatSetting.builder().id("autoshear_radius").displayName("Radius")
			.description("Radius that AutoShear will trigger on Mobs.").defaultValue(5f).minValue(0.1f).maxValue(10f)
			.step(0.1f).build();

	private final BooleanSetting legit = BooleanSetting.builder().id("autoshear_legit").displayName("Legit")
			.description("Whether a raycast will be used to ensure that AutoShear will only shear visible sheep.")
			.defaultValue(false).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("autoshear_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("autoshear_max_rotation").displayName("Max Rotation")
			.description("The max speed that AutoShear will rotate").defaultValue(10.0f).minValue(1.0f).maxValue(360.0f)
			.build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("autoshear_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("autoshear_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	public AutoShear() {
		super("AutoShear");

		setCategory(Category.of("Misc"));
		setDescription("Automatically shears Sheep that are near you.");

		addSetting(radius);
		addSetting(legit);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
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
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		SheepEntity foundEntity = null;
		for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
			if (!(entity instanceof SheepEntity sheep))
				continue;

			// Ensure that the sheap is within a range.
			if (MC.player.squaredDistanceTo(entity) > radius.getValueSqr())
				continue;

			// Get if the sheep is shearable.
            if (!sheep.isShearable() || sheep.isSheared() || sheep.isBaby())
				continue;

			foundEntity = sheep;
			break;
		}

		if (foundEntity != null) {
			// Set the rotation goal to that sheep.
			EntityGoal rotation = EntityGoal.builder().goal(foundEntity).mode(rotationMode.getValue())
					.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
					.yawRandomness(yawRandomness.getValue()).build();
			Aoba.getInstance().rotationManager.setGoal(rotation);

			// Try and find the item slow, and change to it if needed.
			FindItemResult shearItemSlot = findInHotbar(Items.SHEARS);
			if (shearItemSlot.found()) {
				swap(shearItemSlot.slot(), false);
				Hand hand = shearItemSlot.getHand();

				if (legit.getValue()) {
					HitResult ray = MC.crosshairTarget;

					if (ray != null && ray.getType() == HitResult.Type.ENTITY) {
						EntityHitResult entityResult = (EntityHitResult) ray;
						Entity ent = entityResult.getEntity();

						if (ent == foundEntity) {
							MC.player.swingHand(Hand.MAIN_HAND);
							MC.interactionManager.interactEntity(MC.player, foundEntity, hand);
                        }
					}
				} else {
					MC.player.swingHand(Hand.MAIN_HAND);
					MC.interactionManager.interactEntity(MC.player, foundEntity, hand);
                }
			}
		} else
			// No entity found, reset the rotation goal.
			Aoba.getInstance().rotationManager.setGoal(null);
	}
}
