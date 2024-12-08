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
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render3D;
import net.aoba.utils.rotation.Rotation;
import net.aoba.utils.rotation.RotationMode;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class BedAura extends Module implements Render3DListener, TickListener, BlockStateListener {
	private ColorSetting color = ColorSetting.builder().id("nuker_color").displayName("Color").description("Color")
			.defaultValue(new Color(0f, 1f, 1f)).build();

	private FloatSetting radius = FloatSetting.builder().id("nuker_radius").displayName("Radius").description("Radius")
			.defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

	private BooleanSetting legit = BooleanSetting.builder().id("killaura_legit").displayName("Legit")
			.description(
					"Whether a raycast will be used to ensure that KillAura will not hit a player outside of the view")
			.defaultValue(false).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("killaura_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private FloatSetting maxRotation = FloatSetting.builder().id("killaura_max_rotation").displayName("Max Rotation")
			.description("The max speed that KillAura will rotate").defaultValue(10.0f).minValue(1.0f).maxValue(360.0f)
			.build();

	private FloatSetting yawRandomness = FloatSetting.builder().id("killaura_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(60.0f).step(1.0f).build();

	private FloatSetting pitchRandomness = FloatSetting.builder().id("killaura_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(60.0f).step(1.0f).build();

	private BlockPos currentBlockToBreak = null;

	public BedAura() {
		super("BedAura");
		this.setCategory(Category.of("Combat"));
		this.setDescription("Destroys the nearest Bed to the player.");

		this.addSetting(radius);
		this.addSetting(legit);
		this.addSetting(rotationMode);
		this.addSetting(maxRotation);
		this.addSetting(yawRandomness);
		this.addSetting(pitchRandomness);
		this.addSetting(color);
	}

	public void setRadius(int radius) {
		this.radius.setValue((float) radius);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(BlockStateListener.class, this);
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
			Render3D.draw3DBox(event.GetMatrix(), new Box(currentBlockToBreak), color.getValue(), 1.0f);
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
					BlockPos blockpos = new BlockPos(MC.player.getBlockX() + x, (int) MC.player.getBlockY() + y,
							(int) MC.player.getBlockZ() + z);
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

				switch (rotationMode.getValue()) {
				case RotationMode.NONE:
					break;
				case RotationMode.SMOOTH:
					float rotationDegreesPerTick = maxRotation.getValue();

					Rotation rotation = Rotation.getPlayerRotationDeltaFromPosition(currentBlockToBreak.toCenterPos());

					float maxYawRotationDelta = Math.clamp((float) -rotation.yaw(), -rotationDegreesPerTick,
							rotationDegreesPerTick);
					float maxPitchRotation = Math.clamp((float) -rotation.pitch(), -rotationDegreesPerTick,
							rotationDegreesPerTick);

					// Apply Pitch / Yaw randomness and
					double pitchRandom = Math.random() * pitchRandomness.getValue();
					double yawRandom = Math.random() * yawRandomness.getValue();

					maxYawRotationDelta += yawRandom;
					maxPitchRotation += pitchRandom;

					Rotation newRotation = new Rotation(MC.player.getYaw() + maxYawRotationDelta,
							MC.player.getPitch() + maxPitchRotation).roundToGCD();
					MC.player.setYaw((float) newRotation.yaw());
					MC.player.setPitch((float) newRotation.pitch());
					break;
				case RotationMode.INSTANT:
					MC.player.lookAt(EntityAnchor.FEET, currentBlockToBreak.toCenterPos());
					break;
				}

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