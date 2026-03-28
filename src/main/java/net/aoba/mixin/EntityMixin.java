/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.managers.rotation.goals.Goal;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	protected SynchedEntityData entityData;

	@Shadow
	public abstract boolean isEyeInFluid(TagKey<Fluid> fluidTag);

	@Shadow
	public abstract boolean onGround();

	@Inject(at = {
			@At("HEAD") }, method = "isInvisibleTo(Lnet/minecraft/world/entity/player/Player;)Z", cancellable = true)
	private void onIsInvisibleCheck(Player message, CallbackInfoReturnable<Boolean> cir) {
		if (Aoba.getInstance().moduleManager.antiinvis.state.getValue()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(at = { @At("HEAD") }, method = "maxUpStep()F", cancellable = true)
	public void onGetStepHeight(CallbackInfoReturnable<Float> cir) {
    }

	@Inject(at = { @At("HEAD") }, method = "getBlockJumpFactor()F", cancellable = true)
	public void onGetJumpVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
    }

	@Inject(at = { @At("HEAD") }, method = "turn(DD)V", cancellable = true)
	public void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {

	}

	@Redirect(method = "moveRelative", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
	private float onMoveRelative(Entity instance) {
		if (instance == Minecraft.getInstance().player) {
			Goal<?> goal = Aoba.getInstance().rotationManager.getGoal();
			if (goal != null && goal.isMoveFix()) {
				Float serverYaw = Aoba.getInstance().rotationManager.getServerYaw();
				if (serverYaw != null) {
					return serverYaw;
				}
			}
		}
		return instance.getYRot();
	}
}
