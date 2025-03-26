/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Spider extends Module implements TickListener {

	private final FloatSetting speed = FloatSetting.builder().id("spider_speed").displayName("Speed")
			.description("Speed that the player climbs up blocks.").defaultValue(0.1f).minValue(0.05f).maxValue(1f)
			.step(0.05f).build();

	public Spider() {
		super("Spider");
		setCategory(Category.of("Movement"));
		setDescription("Allows players to climb up blocks like a spider.");
		addSetting(speed);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu
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
	public void onTick(Pre event) {
		ClientPlayerEntity player = MC.player;

		if (player.horizontalCollision) {
			Vec3d playerVelocity = player.getVelocity();
			MC.player.setVelocity(new Vec3d(playerVelocity.getX(), speed.getValue(), playerVelocity.getZ()));
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
