/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.render;

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
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;

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
		MatrixStack matrixStack = event.GetMatrix();
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		ItemStack itemStack = MC.player.getMainHandStack();
		if (ModuleUtils.isThrowable(itemStack)) {

			// Get Velocity
			float initialVelocity = (52f);
			if (itemStack.getItem() == Items.BOW && MC.player.isUsingItem())
				initialVelocity *= BowItem.getPullProgress(MC.player.getItemUseTime());

			Camera camera = MC.gameRenderer.getCamera();
			Vec3d offset = Render3D.getEntityPositionOffsetInterpolated(MC.cameraEntity,
					event.getRenderTickCounter().getTickProgress(true));
			Vec3d eyePos = MC.cameraEntity.getEyePos();

			// Calculate look direction.
			Vec3d right = Vec3d.fromPolar(0, camera.getYaw() + 90).multiply(0.14f);
			Vec3d lookDirection = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());
			Vec3d velocity = lookDirection.multiply(initialVelocity).multiply(0.2f);

			// Calculate starting point.
			Vec3d prevPoint = new Vec3d(0, 0, 0).add(eyePos).subtract(offset).add(right);
			Vec3d landPosition = null;

			for (int iteration = 0; iteration < 150; iteration++) {
				Vec3d nextPoint = prevPoint.add(velocity.multiply(0.1));

				// Check to see if we have collided with a block.
				RaycastContext context = new RaycastContext(prevPoint, nextPoint, RaycastContext.ShapeType.COLLIDER,
						FluidHandling.NONE, MC.player);
				BlockHitResult result = MC.world.raycast(context);
				if (result.getType() != HitResult.Type.MISS) {
					// Arrow is collided with a block, draw one last vertice and set land position
					// to the raycast result position.
					landPosition = result.getPos();
					Render3D.drawLine3D(matrixStack, camera, prevPoint, landPosition, renderColor);
					break;
				} else {
					// We did NOT find a collision with a block, check entities.
					Box box = new Box(prevPoint, nextPoint);
					Predicate<Entity> predicate = e -> !e.isSpectator() && e.canHit();
					EntityHitResult entityResult = ProjectileUtil.raycast(MC.player, prevPoint, nextPoint, box,
							predicate, 4096);

					if (entityResult != null && entityResult.getType() != HitResult.Type.MISS) {
						// Arrow is collided with an entity, draw one last vertice and set land position
						// to the raycast result position.
						landPosition = entityResult.getPos();
						Render3D.drawLine3D(matrixStack, camera, prevPoint, landPosition, renderColor);
						break;
					} else {
						// No collisions from raycast, draw next vertice.
						Render3D.drawLine3D(matrixStack, camera, prevPoint, nextPoint, renderColor);
					}
				}

				prevPoint = nextPoint;
				velocity = velocity.multiply(0.99).add(0, throwableGravity(itemStack.getItem()), 0);
			}

			// Draw Cube if a landing position exists.
			if (landPosition != null) {
				float size = blipSize.getValue();
				Vec3d pos1 = landPosition.add(-size, -size, -size);
				Vec3d pos2 = landPosition.add(size, size, size);
				Box box = new Box(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
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