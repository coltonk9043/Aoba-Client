package net.aoba.rendering.msaa;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import org.jspecify.annotations.Nullable;

/**
 * Abstracts MSAA so that it can be supported by all available rendering backends.
 */
public interface IMSAAHandler extends AutoCloseable {

	/** 
	 * Single-sample view + sampler for compositing.
	 */
	record ResolvedTarget(@Nullable GpuTextureView view, @Nullable GpuSampler sampler) {}

	/**
	 * Ensure MSAA resources are allocated at the correct screen dimensions and sample count.
	 **/
	void prepare(int width, int height, int samples);

	/**
	 * Create the render pass for the MSAA
	 **/
	RenderPass beginPass();

	/**
	 * Resolve the multi-sampled content to a target and returns it.
	 **/
	ResolvedTarget resolve();

	@Override
	void close();
}
