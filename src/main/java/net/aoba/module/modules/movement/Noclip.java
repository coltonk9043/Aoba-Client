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
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Noclip extends Module implements TickListener {
	private final FloatSetting flySpeed = FloatSetting.builder().id("noclip_fly_speed").displayName("Speed")
			.description("Fly speed.").defaultValue(2f).minValue(0.1f).maxValue(15f).step(0.5f).build();

	private final FloatSetting speedMultiplier = FloatSetting.builder().id("noclip_speedmultiplier")
			.displayName("Speed Multiplier").description("Noclip speed multiplier.").defaultValue(1.5f).minValue(0.1f)
			.maxValue(15f).step(0.1f).build();

	private final BooleanSetting onGround = BooleanSetting.builder().id("noclip_onground")
			.displayName("On Ground Packet").description("Whether to send the onground packet while moving.")
			.defaultValue(true).build();

	private final FloatSetting packetDistanceThreshold = FloatSetting.builder().id("noclip_packet_distance_threshold")
			.displayName("Packet Distance Threshold").description("Distance threshold for sending packets.")
			.defaultValue(10f).minValue(1f).maxValue(100f).step(1f).build();

	private final FloatSetting packetCountOffset = FloatSetting.builder().id("noclip_packet_count_offset")
			.displayName("Packet Count Offset").description("Offset for the number of packets required.")
			.defaultValue(1f).minValue(1f).maxValue(10f).step(1f).build();

	private final FloatSetting yawOffset = FloatSetting.builder().id("noclip_yaw_offset").displayName("Yaw Offset")
			.description("Angle offset for right direction.").defaultValue(90f).minValue(0f).maxValue(360f).step(1f)
			.build();

	private final FloatSetting maxPackets = FloatSetting.builder().id("noclip_max_packets")
			.displayName("Max Packets Per Update").description("The maximum amount of packets allowed every update.")
			.defaultValue(5f).minValue(1f).maxValue(40f).step(1f).build();

	public Noclip() {
		super("Noclip");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to clip through blocks (Only work clientside).");

		addSetting(flySpeed);
		addSetting(speedMultiplier);
		addSetting(onGround);
		addSetting(packetDistanceThreshold);
		addSetting(packetCountOffset);
		addSetting(yawOffset);
		addSetting(maxPackets);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu
		);
	}

	public void setSpeed(float speed) {
		flySpeed.setValue(speed);
	}

	public float getSpeed() {
		return flySpeed.getValue();
	}

	@Override
	public void onDisable() {
		if (MC.player != null) {
			MC.player.noClip = false;
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

	@Override
	public void onTick(Pre event) {
		ClientPlayerEntity player = MC.player;

		float speed = flySpeed.getDefaultValue();

		if (MC.options.sprintKey.isPressed()) {
			speed *= speedMultiplier.getValue();
		}

		player.setVelocity(new Vec3d(0, 0, 0));

		Vec3d forward = Vec3d.fromPolar(0, player.getYaw());
		Vec3d right = Vec3d.fromPolar(0, player.getYaw() + yawOffset.getValue());

		Vec3d vec = new Vec3d(0, 0, 0);

		if (MC.options.forwardKey.isPressed()) {
			vec = vec.add(forward.multiply(speed));
		} else if (MC.options.backKey.isPressed()) {
			vec = vec.subtract(forward.multiply(speed));
		}

		if (MC.options.rightKey.isPressed()) {
			vec = vec.add(right.multiply(speed));
		} else if (MC.options.leftKey.isPressed()) {
			vec = vec.subtract(right.multiply(speed));
		}

		Vec3d newPos = player.getPos().add(vec);
		int packetsRequired = (int) ((int) Math
				.ceil(MC.player.getPos().distanceTo(newPos) / packetDistanceThreshold.getValue())
				- packetCountOffset.getValue());
		packetsRequired = Math.min(packetsRequired, maxPackets.getValue().intValue());

		for (int i = 0; i < packetsRequired; i++) {
			MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(onGround.getValue(), false));
		}

		MC.player.networkHandler.sendPacket(
				new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, onGround.getValue(), false));
	}

	@Override
	public void onTick(Post event) {

	}
}
