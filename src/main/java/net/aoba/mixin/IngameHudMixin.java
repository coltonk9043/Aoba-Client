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
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.Render2DEvent;
import net.aoba.module.modules.render.NoRender;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.render.RenderManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

@Mixin(InGameHud.class)
public class IngameHudMixin {

	private static final ProjectionMatrix2 matrix = new ProjectionMatrix2("aoba-projection-matrix", -10, 100, true);

	@Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V")
	private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		Profiler profiler = Profilers.get();
		profiler.push("aoba-2d");
		AobaClient client = Aoba.getInstance();
		RenderManager renderManager = RenderManager.getInstance();
		Render2D render2d = renderManager.get2D();
		renderManager.beginFrame(render2d);
		Render2DEvent renderEvent = new Render2DEvent(context, tickCounter);
		render2d.begin();
		client.eventManager.Fire(renderEvent);
		render2d.end();
		renderManager.endFrame();
		profiler.pop();
	}

	@Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
	private void onRenderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;

		if (norender.state.getValue() && norender.getNoVignette())
			ci.cancel();
	}

	@ModifyArgs(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
	private void onRenderPumpkinOverlay(Args args) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;

		if (norender.state.getValue() && norender.getNoPumpkinOverlay())
			args.set(2, 0f);
	}

	@ModifyArgs(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
	private void onRenderPowderSnowOverlay(Args args) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;

		if (norender.state.getValue() && norender.getNoPowderSnowOverlay())
			args.set(2, 0f);
	}

	@Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
	private void onRenderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;

		if (norender.state.getValue() && norender.getNoPortalOverlay())
			ci.cancel();
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (Aoba.getInstance().guiManager.isClickGuiOpen()) {
			ci.cancel();
			return;
		}
		NoRender norender = Aoba.getInstance().moduleManager.norender;
		if (norender.state.getValue() && norender.getNoCrosshair())
			ci.cancel();
	}
}
