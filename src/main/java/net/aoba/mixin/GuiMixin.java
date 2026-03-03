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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.event.events.Render2DEvent;
import net.aoba.module.modules.render.NoRender;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

@Mixin(Gui.class)
public class GuiMixin {
	private static final String POWDER_SNOW_PATH = "textures/misc/powder_snow_outline.png";
	private static final String PUMPKIN_PATH = "textures/misc/pumpkinblur.png";
	
	@Inject(at = @At("TAIL"), method = "renderTabList(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V")
	private void onRenderPlayerList(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
		Render2D.setup();
		try {
			Render2DEvent renderEvent = new Render2DEvent(context, tickCounter);
			Aoba.getInstance().eventManager.Fire(renderEvent);
		} finally {
			Render2D.end();
		}
	}

	@Inject(method = "renderVignette", at = @At("HEAD"), cancellable = true)
	private void onRenderVignetteOverlay(GuiGraphics context, Entity entity, CallbackInfo ci) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;

		if (norender.state.getValue() && norender.getNoVignette())
			ci.cancel();
	}

	@ModifyVariable(method = "renderTextureOverlay", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float modifyOverlayOpacity(float opacity, GuiGraphics context, Identifier identifier) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;
		if (norender == null || !norender.state.getValue())
			return opacity;

		String path = identifier.getPath();

		if (path.equals(PUMPKIN_PATH) && norender.getNoPumpkinOverlay()) {
			return 0f;
		}

		if (path.equals(POWDER_SNOW_PATH) && norender.getNoPowderSnowOverlay()) {
			return 0f;
		}

		return opacity;
	}

	@Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
	private void onRenderPortalOverlay(GuiGraphics context, float nauseaStrength, CallbackInfo ci) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;

		if (norender.state.getValue() && norender.getNoPortalOverlay())
			ci.cancel();
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void onRenderCrosshair(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
		if (Aoba.getInstance().guiManager.isClickGuiOpen()) {
			ci.cancel();
			return;
		}
		NoRender norender = Aoba.getInstance().moduleManager.norender;
		if (norender.state.getValue() && norender.getNoCrosshair())
			ci.cancel();
	}
}
