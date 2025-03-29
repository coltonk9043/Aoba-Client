package net.aoba.utils.render;

import java.util.OptionalDouble;
import java.util.function.Function;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

public class RenderLayers {

	// 3D Render Layers
	public static final RenderLayer.MultiPhase QUADS = RenderLayer.of("aoba:quads", 2000, false, true,
			AobaRenderPipelines.QUADS, RenderLayer.MultiPhaseParameters.builder().build(false));

	public static final RenderLayer.MultiPhase TRIS = RenderLayer.of("aoba:tris", 786432, false, true,
			AobaRenderPipelines.TRIS, RenderLayer.MultiPhaseParameters.builder().build(false));

	public static final RenderLayer.MultiPhase LINES = RenderLayer.of("aoba:lines", 2000, AobaRenderPipelines.LINES,
			RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(1)))
					.build(false));

	// 2D Render Layers
	public static final RenderLayer.MultiPhase QUADS_GUI = RenderLayer.of("aoba:quads_gui", 786432, false, true,
			AobaRenderPipelines.QUADS_GUI, RenderLayer.MultiPhaseParameters.builder().build(false));

	public static final Function<Identifier, RenderLayer> TEXTURES_QUADS_GUI = Util
			.memoize((Function<Identifier, RenderLayer>) (texture -> RenderLayer.of("aoba:textured_quads_gui", 1536,
					false, false, RenderPipelines.FIRE_SCREEN_EFFECT, RenderLayer.MultiPhaseParameters.builder()
							.texture(new RenderPhase.Texture(texture, TriState.FALSE, false)).build(false))));

	public static final RenderLayer.MultiPhase TRIS_GUI = RenderLayer.of("aoba:tris_gui", 786432, false, true,
			AobaRenderPipelines.TRIS_GUI, RenderLayer.MultiPhaseParameters.builder().build(false));

	public static final RenderLayer.MultiPhase LINES_GUI = RenderLayer.of("aoba:lines_gui", 786432, false, true,
			AobaRenderPipelines.LINES_GUI, RenderLayer.MultiPhaseParameters.builder()
					.lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(1))).build(false));

}
