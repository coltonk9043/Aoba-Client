package net.aoba.utils.render;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class AobaRenderPipelines {

	public static final List<RenderPipeline> PIPELINES = new ArrayList<>();

	// 3D Pipelines
	public static final RenderPipeline QUADS = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader(Identifier.of("aoba", "shaders/pos_color.vert"))
			.withFragmentShader(Identifier.of("aoba", "shaders/pos_color.frag")).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_quads").build());

	public static final RenderPipeline TRIS = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader(Identifier.of("aoba", "shaders/pos_color.vert"))
			.withFragmentShader(Identifier.of("aoba", "shaders/pos_color.frag")).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_tris").build());

	public static final RenderPipeline LINES = RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader(Identifier.of("aoba", "shaders/pos_color.vert"))
			.withFragmentShader(Identifier.of("aoba", "shaders/pos_color.frag")).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.LINES)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withLocation("pipeline/aoba_lines").build());

	// 2D Pipelines
	public static final RenderPipeline TRIS_GUI = addPipeline(RenderPipelines.register(RenderPipeline.builder()
			.withVertexShader(Identifier.of("aoba", "shaders/pos_color.vert"))
			.withFragmentShader(Identifier.of("aoba", "shaders/pos_color.frag")).withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES).withCull(false).withDepthWrite(false)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation(Identifier.of("aoba", "pipeline/aoba_tris_gui")).build()));

	public static final RenderPipeline LINES_GUI = addPipeline(RenderPipelines
			.register(RenderPipeline.builder().withLocation(Identifier.of("aoba", "pipeline/aoba_lines_gui"))
					.withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.LINES)
					.withVertexShader(Identifier.of("aoba", "shaders/pos_color.vert"))
					.withFragmentShader(Identifier.of("aoba", "shaders/pos_color.frag"))
					.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false)
					.withBlend(BlendFunction.TRANSLUCENT).withCull(true).build()));

	public static RenderPipeline addPipeline(RenderPipeline pipeline) {
		PIPELINES.add(pipeline);
		return pipeline;
	}

	// Thanks Meteor! I needed this.
	public static void precompile() {
		GpuDevice device = RenderSystem.getDevice();
		ResourceManager resources = MinecraftClient.getInstance().getResourceManager();

		for (RenderPipeline pipeline : PIPELINES) {
			device.precompilePipeline(pipeline, (identifier, shaderType) -> {
				var resource = resources.getResource(identifier).get();

				try (var in = resource.getInputStream()) {
					return IOUtils.toString(in, StandardCharsets.UTF_8);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

}
