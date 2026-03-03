package net.aoba.utils.render;

import java.util.function.Function;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class RenderLayers {

	// 3D Render Layers using the new RenderSetup/RenderPipeline system
	public static final RenderType QUADS = RenderType.create(
			"aoba_quads",
			RenderSetup.builder(AobaRenderPipelines.QUADS).createRenderSetup());

	public static final RenderType LINES = RenderType.create(
			"aoba_lines",
			RenderSetup.builder(AobaRenderPipelines.LINES).createRenderSetup());

	// 2D Render Layers - using our custom pipelines
	public static final RenderType QUADS_GUI = RenderType.create(
			"aoba_quads_gui",
			RenderSetup.builder(AobaRenderPipelines.QUADS_GUI).createRenderSetup());

	public static final RenderType LINES_GUI = RenderType.create(
			"aoba_lines_gui",
			RenderSetup.builder(AobaRenderPipelines.LINES_GUI).createRenderSetup());

	public static final RenderType TRIS_GUI = RenderType.create(
			"aoba_tris_gui",
			RenderSetup.builder(AobaRenderPipelines.TRIS_GUI).createRenderSetup());

	// Textured GUI layers with texture binding
	public static final Function<Identifier, RenderType> TEXTURES_QUADS_GUI = Util
			.memoize((Function<Identifier, RenderType>) (texture -> RenderType.create(
					"aoba_textured_quads_gui",
					RenderSetup.builder(AobaRenderPipelines.TEXTURED_QUADS_GUI)
							.withTexture("Sampler0", texture)
							.createRenderSetup())));

	public static final Function<Identifier, RenderType> TEXTURES_TRIS_GUI = Util
			.memoize((Function<Identifier, RenderType>) (texture -> RenderType.create(
					"aoba_textured_tris_gui",
					RenderSetup.builder(AobaRenderPipelines.TEXTURED_TRIS_GUI)
							.withTexture("Sampler0", texture)
							.createRenderSetup())));

}
