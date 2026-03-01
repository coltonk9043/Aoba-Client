/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Predicate;

import org.joml.Matrix4f;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.aoba.utils.render.Render3D;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Trajectory extends Module implements Render3DListener {

	private final ColorSetting color = ColorSetting.builder().id("trajectory_color").displayName("Color")
			.description("Color").defaultValue(new Color(0f, 1f, 1f)).build();

	private final FloatSetting blipSize = FloatSetting.builder().id("trajectory_blipsize").displayName("Blip Size")
			.description("Blip Size").defaultValue(0.15f).minValue(0.05f).maxValue(1f).step(0.05f).build();

	public Trajectory() {
		super("Trajectory");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see where their projectiles will approxmiately land.");

		addSetting(color);
		addSetting(blipSize);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		Color renderColor = color.getValue();
		PoseStack matrixStack = event.GetMatrix();
		Matrix4f matrix = matrixStack.last().pose();

		ItemStack itemStack = MC.player.getMainHandItem();
		if (ModuleUtils.isThrowable(itemStack)) {

			// Get Velocity
			float initialVelocity = (52f);
			if (itemStack.getItem() == Items.BOW && MC.player.isUsingItem())
				initialVelocity *= BowItem.getPowerForTime(MC.player.getTicksUsingItem());

			Camera camera = MC.gameRenderer.getMainCamera();
			Vec3 offset = Render3D.getEntityPositionOffsetInterpolated(MC.cameraEntity,
					event.getRenderTickCounter().getGameTimeDeltaPartialTick(true));
			Vec3 eyePos = MC.cameraEntity.getEyePosition();

			// Calculate look direction.
			Vec3 right = Vec3.directionFromRotation(0, camera.yRot() + 90).scale(0.14f);
			Vec3 lookDirection = Vec3.directionFromRotation(camera.xRot(), camera.yRot());
			Vec3 velocity = lookDirection.scale(initialVelocity).scale(0.2f);

			// Calculate starting point.
			Vec3 prevPoint = new Vec3(0, 0, 0).add(eyePos).subtract(offset).add(right);
			Vec3 landPosition = null;

			for (int iteration = 0; iteration < 150; iteration++) {
				Vec3 nextPoint = prevPoint.add(velocity.scale(0.1));

				// Check to see if we have collided with a block.
				ClipContext context = new ClipContext(prevPoint, nextPoint, ClipContext.Block.COLLIDER,
						Fluid.NONE, MC.player);
				BlockHitResult result = MC.level.clip(context);
				if (result.getType() != HitResult.Type.MISS) {
					// Arrow is collided with a block, draw one last vertice and set land position
					// to the raycast result position.
					landPosition = result.getLocation();
					Render3D.drawLine3D(matrixStack, camera, prevPoint, landPosition, renderColor);
					break;
				} else {
					// We did NOT find a collision with a block, check entities.
					AABB box = new AABB(prevPoint, nextPoint);
					Predicate<Entity> predicate = e -> !e.isSpectator() && e.isPickable();
					EntityHitResult entityResult = ProjectileUtil.getEntityHitResult(MC.player, prevPoint, nextPoint, box,
							predicate, 4096);

					if (entityResult != null && entityResult.getType() != HitResult.Type.MISS) {
						// Arrow is collided with an entity, draw one last vertice and set land position
						// to the raycast result position.
						landPosition = entityResult.getLocation();
						Render3D.drawLine3D(matrixStack, camera, prevPoint, landPosition, renderColor);
						break;
					} else {
						// No collisions from raycast, draw next vertice.
						Render3D.drawLine3D(matrixStack, camera, prevPoint, nextPoint, renderColor);
					}
				}

				prevPoint = nextPoint;
				velocity = velocity.scale(0.99).add(0, throwableGravity(itemStack.getItem()), 0);
			}

			// Draw Cube if a landing position exists.
			if (landPosition != null) {
				float size = blipSize.getValue();
				Vec3 pos1 = landPosition.add(-size, -size, -size);
				Vec3 pos2 = landPosition.add(size, size, size);
				AABB box = new AABB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
				Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), box, renderColor, 1.0f);
			}
		}
	}

	public double throwableGravity(Item item) {
		if (item == Items.BOW)
			return -0.045f;
		else
			return -0.13f;
	}
}