package net.aoba.rendering.msaa;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.Minecraft;

/**
 * OpenGL MSAA handler.
 */
public class OpenGLMSAAHandler implements IMSAAHandler {
	private static final Minecraft MC = Minecraft.getInstance();

	private int msaaFbo = -1;
	private int msaaColorRbo = -1;
	private int resolveFbo = -1;
	private int currentWidth;
	private int currentHeight;
	private int currentSamples;

	private @Nullable GpuTexture resolveTexture;
	private @Nullable GpuTextureView resolveTextureView;
	private @Nullable GpuSampler resolveSampler;

	@Override
	public void prepare(int width, int height, int samples) {
		if (msaaFbo != -1 && currentWidth == width && currentHeight == height && currentSamples == samples)
			return;

		releaseBuffers();

		msaaColorRbo = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, msaaColorRbo);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL30.GL_RGBA8, width, height);

		msaaFbo = GL30.glGenFramebuffers();
		GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, msaaFbo);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER,
				msaaColorRbo);
		
		resolveTexture = RenderSystem.getDevice().createTexture("aoba_msaa_resolve",
				GpuTexture.USAGE_RENDER_ATTACHMENT | GpuTexture.USAGE_TEXTURE_BINDING, TextureFormat.RGBA8, width,
				height, 1, 1);
		resolveTextureView = RenderSystem.getDevice().createTextureView(resolveTexture);
		resolveFbo = GL30.glGenFramebuffers();
		GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, resolveFbo);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D,
				((GlTexture) resolveTexture).glId(), 0);

		resolveSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);

		GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

		currentWidth = width;
		currentHeight = height;
		currentSamples = samples;
	}

	@Override
	public RenderPass beginPass() {
		// Open a render pass on the main render target then
		// immediately redirect to the MSAA framebuffer.
		RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Aoba MSAA",
				MC.getMainRenderTarget().getColorTextureView(), OptionalInt.empty(),
				MC.getMainRenderTarget().useDepth ? MC.getMainRenderTarget().getDepthTextureView() : null,
				OptionalDouble.empty());

		GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, msaaFbo);
		GlStateManager._viewport(0, 0, currentWidth, currentHeight);
		GlStateManager._disableScissorTest();
		GlStateManager._colorMask(ColorTargetState.WRITE_ALL);
		GL11.glClearColor(0f, 0f, 0f, 0f);
		GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT);

		return pass;
	}

	@Override
	public ResolvedTarget resolve() {
		GlStateManager._disableScissorTest();
		GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, msaaFbo);
		GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, resolveFbo);
		GlStateManager._glBlitFrameBuffer(0, 0, currentWidth, currentHeight, 0, 0, currentWidth, currentHeight,
				GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
		GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		return new ResolvedTarget(resolveTextureView, resolveSampler);
	}

	private void releaseBuffers() {
		if (msaaFbo != -1) {
			GL30.glDeleteFramebuffers(msaaFbo);
			msaaFbo = -1;
		}
		if (msaaColorRbo != -1) {
			GL30.glDeleteRenderbuffers(msaaColorRbo);
			msaaColorRbo = -1;
		}
		if (resolveFbo != -1) {
			GL30.glDeleteFramebuffers(resolveFbo);
			resolveFbo = -1;
		}
		if (resolveTextureView != null) {
			resolveTextureView.close();
			resolveTextureView = null;
		}
		if (resolveTexture != null) {
			resolveTexture.close();
			resolveTexture = null;
		}
		resolveSampler = null;
		currentWidth = 0;
		currentHeight = 0;
		currentSamples = 0;
	}

	@Override
	public void close() {
		releaseBuffers();
	}
}
