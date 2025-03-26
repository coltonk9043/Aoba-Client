/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.render.Camera;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ClickTP extends Module implements MouseClickListener {

	private final FloatSetting distance = FloatSetting.builder().id("clicktp_distance").displayName("Max Distance")
			.description("Max Distance to teleport.").defaultValue(10f).minValue(1.0f).maxValue(200f).step(1.0f)
			.build();

	public ClickTP() {
		super("ClickTP");
		setCategory(Category.of("Movement"));
		setDescription("Allows the user to teleport where they are looking.");

		addSetting(distance);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onMouseClick(MouseClickEvent event) {

		if (event.button == MouseButton.RIGHT && event.action == MouseAction.DOWN) {
			Camera camera = MC.gameRenderer.getCamera();

			if (camera != null) {
				Vec3d direction = Vec3d.fromPolar(camera.getPitch(), camera.getYaw()).multiply(210);
				Vec3d targetPos = camera.getPos().add(direction);

				RaycastContext context = new RaycastContext(camera.getPos(), targetPos,
						RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, MC.player);

				HitResult raycast = MC.world.raycast(context);

				if (raycast.getType() == HitResult.Type.BLOCK) {
					BlockHitResult raycastBlock = (BlockHitResult) raycast;
					BlockPos pos = raycastBlock.getBlockPos();
					Direction side = raycastBlock.getSide();

					Vec3d newPos = new Vec3d(pos.getX() + 0.5 + side.getOffsetX(), pos.getY() + 1,
							pos.getZ() + 0.5 + side.getOffsetZ());
					int packetsRequired = (int) Math.ceil(MC.player.getPos().distanceTo(newPos) / 10) - 1;

					for (int i = 0; i < packetsRequired; i++) {
						MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, false));
					}

					MC.player.networkHandler.sendPacket(
							new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, true, false));
					MC.player.setPosition(newPos);
				}
			}
		}
	}
}
