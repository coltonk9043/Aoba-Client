package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.aoba.Aoba;
import net.aoba.managers.rotation.goals.Goal;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
	private Vec3 onGetLookAngle(net.minecraft.world.entity.LivingEntity instance) {
		Minecraft mc = Minecraft.getInstance();
		if (instance == mc.player) {
			Goal<?> goal = Aoba.getInstance().rotationManager.getGoal();
			if (goal != null && goal.isFakeRotation()) {
				Float serverYaw = Aoba.getInstance().rotationManager.getServerYaw();
				Float serverPitch = Aoba.getInstance().rotationManager.getServerPitch();
				if (serverYaw != null && serverPitch != null) {
					return instance.calculateViewVector(serverPitch, serverYaw);
				}
			}
		}
		return instance.getLookAngle();
	}
}
