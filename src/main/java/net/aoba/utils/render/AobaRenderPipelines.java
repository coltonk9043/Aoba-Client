package net.aoba.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderPipelines;

public class AobaRenderPipelines {
	// 3D Pipelines
	public static final RenderPipeline QUADS = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withCull(false).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, Mode.QUADS)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_quads").build());

	public static final RenderPipeline TRIS = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withCull(false).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, Mode.TRIANGLES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_tris").build());

	public static final RenderPipeline LINES = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_color").withFragmentShader("core/position_color")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withBlend(BlendFunction.TRANSLUCENT).withVertexFormat(DefaultVertexFormat.POSITION_COLOR, Mode.DEBUG_LINES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_lines").build());

	// 2D GUI Pipelines
	public static final RenderPipeline QUADS_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/gui").withFragmentShader("core/gui")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withCull(false).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, Mode.QUADS)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation("pipeline/aoba_quads_gui").build());

	public static final RenderPipeline TEXTURED_QUADS_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withSampler("Sampler0").withCull(false).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, Mode.QUADS)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation("pipeline/aoba_textured_quads_gui").build());

	public static final RenderPipeline TEXTURED_TRIS_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withSampler("Sampler0").withCull(false).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, Mode.TRIANGLES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation("pipeline/aoba_textured_tris_gui").build());

	public static final RenderPipeline TRIS_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/gui").withFragmentShader("core/gui")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withCull(false).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, Mode.TRIANGLES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation("pipeline/aoba_tris_gui").build());

	public static final RenderPipeline LINES_GUI = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader("core/gui").withFragmentShader("core/gui")
			.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
			.withUniform("Projection", UniformType.UNIFORM_BUFFER)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, Mode.DEBUG_LINE_STRIP)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation("pipeline/aoba_lines_gui").build());
}
