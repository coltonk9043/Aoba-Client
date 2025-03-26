/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.BlockStateEvent;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.BlockStateListener;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.managers.rotation.Rotation;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.RotationGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class BedAura extends Module implements Render3DListener, TickListener, BlockStateListener {
	private final ColorSetting color = ColorSetting.builder().id("nuker_color").displayName("Color")
			.description("Color").defaultValue(new Color(0f, 1f, 1f)).build();

	private final FloatSetting radius = FloatSetting.builder().id("nuker_radius").displayName("Radius")
			.description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

	private final BooleanSetting legit = BooleanSetting.builder().id("killaura_legit").displayName("Legit")
			.description(
					"Whether a raycast will be used to ensure that KillAura will not hit a player outside of the view")
			.defaultValue(false).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("killaura_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("killaura_max_rotation")
			.displayName("Max Rotation").description("The max speed that KillAura will rotate").defaultValue(10.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("killaura_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("killaura_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private BlockPos currentBlockToBreak = null;

	public BedAura() {
		super("BedAura");
		setCategory(Category.of("Combat"));
		setDescription("Destroys the nearest Bed to the player.");

		addSetting(radius);
		addSetting(legit);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
		addSetting(color);
	}

	public void setRadius(int radius) {
		this.radius.setValue((float) radius);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(BlockStateListener.class, this);
		Aoba.getInstance().rotationManager.setGoal(null);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(BlockStateListener.class, this);
	}

	@Override
	public void onToggle() {
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (currentBlockToBreak != null) {
			Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), new Box(currentBlockToBreak), color.getValue(),
					1.0f);
		}
	}

	@Override
	public void onBlockStateChanged(BlockStateEvent event) {
		if (currentBlockToBreak != null) {
			BlockPos blockPos = event.getBlockPos();
			BlockState oldBlockState = event.getPreviousBlockState();
			if (blockPos.equals(currentBlockToBreak) && (oldBlockState.isAir())) {
				currentBlockToBreak = null;
			}
		}
	}

	private BlockPos getNextBlock() {
		// Scan to find next block to begin breaking.
		int rad = radius.getValue().intValue();
		for (int y = rad; y > -rad; y--) {
			for (int x = -rad; x < rad; x++) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(MC.player.getBlockX() + x, MC.player.getBlockY() + y,
							MC.player.getBlockZ() + z);
					Block block = MC.world.getBlockState(blockpos).getBlock();
					if (!isBed(block))
						continue;

					return blockpos;
				}
			}
		}
		return null;
	}

	@Override
	public void onTick(Pre event) {
		if (currentBlockToBreak == null) {
			currentBlockToBreak = getNextBlock();
		}

		if (currentBlockToBreak != null) {
			// Check to ensure that the block is not further than we can reach.
			int range = (int) (Math.floor(radius.getValue()) + 1);
			int rangeSqr = range ^ 2;

			if (MC.player.getBlockPos().toCenterPos().distanceTo(currentBlockToBreak.toCenterPos()) > rangeSqr) {
				currentBlockToBreak = null;
			} else {

				RotationGoal rotation = RotationGoal.builder()
						.goal(Rotation.rotationFrom(currentBlockToBreak.toCenterPos())).mode(rotationMode.getValue())
						.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
						.yawRandomness(yawRandomness.getValue()).build();
				Aoba.getInstance().rotationManager.setGoal(rotation);

				if (legit.getValue()) {
					HitResult ray = MC.crosshairTarget;

					if (ray != null && ray.getType() == HitResult.Type.BLOCK) {
						BlockHitResult blockResult = (BlockHitResult) ray;

						if (currentBlockToBreak.equals(blockResult.getBlockPos())) {
							MC.player.swingHand(Hand.MAIN_HAND);
							breakBlock(currentBlockToBreak);
						}
					}
				} else {
					MC.player.swingHand(Hand.MAIN_HAND);
					breakBlock(currentBlockToBreak);
				}
			}

		}
	}

	private void breakBlock(BlockPos pos) {
		MC.player.networkHandler
				.sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, pos, Direction.NORTH));
		MC.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, pos, Direction.NORTH));
		MC.player.swingHand(Hand.MAIN_HAND);
	}

	@Override
	public void onTick(Post event) {

	}

	private boolean isBed(Block block) {
		return block instanceof BedBlock;
	}
}