package net.aoba.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class AobaPanorama {
	public static final Identifier PANORAMA_OVERLAY = Identifier.withDefaultNamespace("textures/gui/title/background/panorama_overlay.png");
	private float spin;

	public void extractRenderState(final GuiGraphicsExtractor graphics, final int width, final int height, final boolean shouldSpin) {
		Minecraft minecraft = Minecraft.getInstance();
		if (shouldSpin) {
			float delta = minecraft.getDeltaTracker().getRealtimeDeltaTicks();
			float speed = (float) (delta * minecraft.gameRenderer.getGameRenderState().optionsRenderState.panoramaSpeed);
			this.spin = Mth.wrapDegrees(this.spin + speed * 0.1f);
		}

		minecraft.gameRenderer.getGameRenderState().guiRenderState.panoramaRenderState = new PanoramaRenderState(-this.spin);
		graphics.blit(RenderPipelines.GUI_TEXTURED, PANORAMA_OVERLAY, 0, 0, 0.0f, 0.0f, width, height, 16, 128, 16, 128);
	}
}
