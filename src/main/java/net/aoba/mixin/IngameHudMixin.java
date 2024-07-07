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

import net.aoba.Aoba;
import net.aoba.event.events.Render2DEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class IngameHudMixin {

<<<<<<< Updated upstream
    @Inject(at = {@At(value = "TAIL")}, method = {"render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"})
    private void onRender(DrawContext context, RenderTickCounter tickDelta, CallbackInfo ci) {
        Aoba.getInstance().drawHUD(context, tickDelta.getTickDelta(false));
    }
=======
	@Inject(at = {@At(value = "TAIL") }, method = {"render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V" })
	private void onRender(DrawContext context, RenderTickCounter tickDelta, CallbackInfo ci) {
		// Fire Render 2D event
		Render2DEvent renderEvent = new Render2DEvent(context, tickDelta);
		Aoba.getInstance().eventManager.Fire(renderEvent);
	}
>>>>>>> Stashed changes
}
