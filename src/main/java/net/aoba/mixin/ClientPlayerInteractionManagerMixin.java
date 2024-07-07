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

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    // TODO Reach?
    /*
     * @Inject(at = { @At("HEAD") }, method = { "getReachDistance()F" }, cancellable
     * = true) private void onGetReachDistance(CallbackInfoReturnable<Float> ci) {
     * Reach reachHack = (Reach) Aoba.getInstance().moduleManager.reach; if
     * (reachHack.getState()) { ci.setReturnValue(reachHack.getReach()); } }
     *
     * @Inject(at = { @At("HEAD") }, method = { "hasExtendedReach()Z" }, cancellable
     * = true) private void hasExtendedReach(CallbackInfoReturnable<Boolean> cir) {
     * Reach reachHack = (Reach) Aoba.getInstance().moduleManager.reach; if
     * (reachHack.getState()) cir.setReturnValue(true); }
     */
}
