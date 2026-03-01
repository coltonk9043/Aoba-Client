/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.world.phys.Vec2;

public class Strafe extends Module implements TickListener {

	private final FloatSetting intensity = FloatSetting.builder().id("strafe_intensity").displayName("Intensity")
			.description("Strafe intensity.").defaultValue(0.1f).minValue(0f).maxValue(0.3f).step(0.1f).build();

	public Strafe() {
		super("Strafe");
		setCategory(Category.of("Movement"));
		setDescription("Makes the user able to change directions mid-air");

		addSetting(intensity);

		setDetectable(AntiCheat.NoCheatPlus, AntiCheat.Vulcan, AntiCheat.AdvancedAntiCheat, AntiCheat.Verus,
				AntiCheat.Grim, AntiCheat.Matrix, AntiCheat.Negativity, AntiCheat.Karhu);
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
		Vec2 playerInput = MC.player.input.getMoveVector();
		if (playerInput.x != 0 || playerInput.y != 0) {

			if (MC.player.onGround() && MC.options.keyJump.isDown())
				MC.player.push(0, MC.player.getDeltaMovement().y, 0);

			if (MC.player.onGround())
				return;

			float speed;
			if (!MC.player.onGround())
				speed = (float) Math.sqrt(MC.player.getDeltaMovement().x * MC.player.getDeltaMovement().x
						+ MC.player.getDeltaMovement().z * MC.player.getDeltaMovement().z + intensity.getValue());
			else
				speed = MC.player.getSpeed();

			float yaw = MC.player.getYRot();
			float forward = 1;

			if (MC.player.zza < 0) {
				yaw += 180;
				forward = -0.5f;
			} else if (MC.player.zza > 0)
				forward = 0.5f;

			if (MC.player.xxa > 0)
				yaw -= 90 * forward;
			if (MC.player.xxa < 0)
				yaw += 90 * forward;

			yaw = (float) Math.toRadians(yaw);

			MC.player.setDeltaMovement(-Math.sin(yaw) * speed, MC.player.getDeltaMovement().y, Math.cos(yaw) * speed);

		}
	}

	@Override
	public void onTick(TickEvent.Post event) {

	}
}
