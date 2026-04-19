package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.buffers.GpuBufferSlice;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@Unique
	private final CubeMap aoba$cubeMap = new CubeMap(Identifier.fromNamespaceAndPath("aoba", "textures/mainmenu/panorama"));

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderTail(GpuBufferSlice gpuBufferSlice, CallbackInfo ci) {
		if (Aoba.getInstance() != null && Aoba.getInstance().render2D != null) {
			Aoba.getInstance().render2D.render();
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/CubeMap;render(FF)V"))
	private void aoba$redirectPanoramaCubeMap(CubeMap instance, float rotX, float rotY) {
		boolean useAoba = Aoba.getInstance() != null && GuiManager.enableCustomTitle.getValue();
		if(useAoba) {
			this.aoba$cubeMap.render(rotX, rotY);
		}else {
			instance.render(rotX, rotY);
		}
	}

	@Inject(method = "registerPanoramaTextures", at = @At("TAIL"))
	private void aoba$registerPanoramaTextures(TextureManager textureManager, CallbackInfo ci) {
		this.aoba$cubeMap.registerTextures(textureManager);
	}

	@Inject(method = "close", at = @At("TAIL"))
	private void aoba$closePanoramaCubeMap(CallbackInfo ci) {
		this.aoba$cubeMap.close();
	}
}
