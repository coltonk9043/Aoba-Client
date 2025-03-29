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
import net.minecraft.util.math.Vec2f;

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
		Vec2f playerInput = MC.player.input.getMovementInput();
		if (playerInput.x != 0 || playerInput.y != 0) {

			if (MC.player.isOnGround() && MC.options.jumpKey.isPressed())
				MC.player.addVelocity(0, MC.player.getVelocity().y, 0);

			if (MC.player.isOnGround())
				return;

			float speed;
			if (!MC.player.isOnGround())
				speed = (float) Math.sqrt(MC.player.getVelocity().x * MC.player.getVelocity().x
						+ MC.player.getVelocity().z * MC.player.getVelocity().z + intensity.getValue());
			else
				speed = MC.player.getMovementSpeed();

			float yaw = MC.player.getYaw();
			float forward = 1;

			if (MC.player.forwardSpeed < 0) {
				yaw += 180;
				forward = -0.5f;
			} else if (MC.player.forwardSpeed > 0)
				forward = 0.5f;

			if (MC.player.sidewaysSpeed > 0)
				yaw -= 90 * forward;
			if (MC.player.sidewaysSpeed < 0)
				yaw += 90 * forward;

			yaw = (float) Math.toRadians(yaw);

			MC.player.setVelocity(-Math.sin(yaw) * speed, MC.player.getVelocity().y, Math.cos(yaw) * speed);

		}
	}

	@Override
	public void onTick(TickEvent.Post event) {

	}
}
