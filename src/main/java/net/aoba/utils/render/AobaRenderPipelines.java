package net.aoba.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;

public class AobaRenderPipelines {
	// 3D Pipelines
	public static final RenderPipeline QUADS = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withBlend(BlendFunction.TRANSLUCENT).withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_quads").build());

	public static final RenderPipeline TRIS = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withBlend(BlendFunction.TRANSLUCENT).withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_tris").build());

	public static final RenderPipeline LINES = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withBlend(BlendFunction.TRANSLUCENT).withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_lines").build());

	// 2D Pipelines
	public static final RenderPipeline QUADS_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withBlend(BlendFunction.TRANSLUCENT).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS).withCull(false)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_quads_gui").build());

	public static final RenderPipeline TEXTURED_QUADS_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color")
			.withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS)
			.withLocation("pipeline/aoba_textured_quads_gui").withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withDepthWrite(false).build());

	public static final RenderPipeline TRIS_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withBlend(BlendFunction.TRANSLUCENT).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES).withCull(false)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_tris_gui").build());

	public static final RenderPipeline LINES_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withBlend(BlendFunction.TRANSLUCENT).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.DEBUG_LINE_STRIP).withCull(false)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_lines_gui").build());
}
