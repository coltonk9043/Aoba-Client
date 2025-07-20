package net.aoba.utils.render;

import java.util.OptionalInt;

import org.joml.Matrix3x2fStack;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.core.BufferManager;
import net.aoba.utils.render.core.IRenderer;
import net.aoba.utils.render.mesh.UboData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexFormats;

public class Render2D implements IRenderer {
	private final DynamicUniformStorage<UboData> UNIFORM_STORAGE = new DynamicUniformStorage<>("Aoba 2D UBO",
			new Std140SizeCalculator().putMat4f().putMat4f().get(), 16);
	private final UboData UBO_DATA = new UboData();

	private final BufferManager triangleBuffer;
	private final BufferManager lineBuffer;
	private boolean isBuilding = false;
	private int currentVertexIndex = 0;

	public Render2D() {
		this.triangleBuffer = new BufferManager(28);
		this.lineBuffer = new BufferManager(28);
	}

	@Override
	public void begin() {
		if (isBuilding) {
			throw new IllegalStateException("Renderer is already building");
		}

		isBuilding = true;
		triangleBuffer.resetAfterRender();
		lineBuffer.resetAfterRender();
		currentVertexIndex = 0;
	}

	@Override
	public void end() {
		if (!isBuilding) {
			throw new IllegalStateException("Renderer is not building");
		}
		isBuilding = false;
	}

	@Override
	public boolean isBuilding() {
		return isBuilding;
	}

	@Override
	public void reset() {
		triangleBuffer.resetAfterRender();
		lineBuffer.resetAfterRender();
		currentVertexIndex = 0;
	}

	@Override
	public void render() {
		System.out.println("2D Render called - triangles: v=" + triangleBuffer.getVertexCount() + " i="
				+ triangleBuffer.getIndexCount() + ", lines: v=" + lineBuffer.getVertexCount() + " i="
				+ lineBuffer.getIndexCount());
		try {
			if (!triangleBuffer.isEmpty()) {
				renderBuffer(triangleBuffer, AobaRenderPipelines.TRIS_GUI);
			}
			if (!lineBuffer.isEmpty()) {
				renderBuffer(lineBuffer, AobaRenderPipelines.LINES_GUI);
			}
		} finally {
			reset();
		}
	}

	private void renderBuffer(BufferManager buffer, RenderPipeline pipeline) {
		if (buffer.getVertexCount() == 0 || buffer.getIndexCount() == 0) {
			return;
		}

		Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();

		GpuTextureView colorAttachment = framebuffer.getColorAttachmentView();
		GpuBuffer vertexBuffer = buffer.createVertexBuffer(VertexFormats.POSITION_COLOR);
		GpuBuffer indexBuffer = buffer.createIndexBuffer(VertexFormats.POSITION_COLOR);

		UBO_DATA.proj = RenderManager.projection;
		UBO_DATA.modelView = RenderSystem.getModelViewMatrix();

		GpuBufferSlice matrixData = UNIFORM_STORAGE.write(UBO_DATA);

		RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Aoba 2D Renderer",
				colorAttachment, OptionalInt.empty());

		pass.setPipeline(pipeline);
		pass.setUniform("Matrices", matrixData);
		pass.setVertexBuffer(0, vertexBuffer);
		pass.setIndexBuffer(indexBuffer, VertexFormat.IndexType.INT);
		pass.drawIndexed(0, 0, buffer.getIndexCount(), 1);
		pass.close();
	}

	public void drawBox(float x, float y, float width, float height, Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		int startVertex = currentVertexIndex;

		triangleBuffer.addVertex(x, y, 0);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex(x + width, y, 0);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex(x + width, y + height, 0);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex(x, y + height, 0);
		triangleBuffer.addColor(color);

		triangleBuffer.addTriangle(startVertex, startVertex + 1, startVertex + 2);
		triangleBuffer.addTriangle(startVertex, startVertex + 2, startVertex + 3);

		currentVertexIndex += 4;
	}

	public void drawBox(Rectangle rect, Color color) {
		drawBox(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), color);
	}

	public void drawBoxOutline(float x, float y, float width, float height, Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		int startVertex = currentVertexIndex;

		lineBuffer.addVertex(x, y, 0);
		lineBuffer.addColor(color);
		lineBuffer.addVertex(x + width, y, 0);
		lineBuffer.addColor(color);
		lineBuffer.addVertex(x + width, y + height, 0);
		lineBuffer.addColor(color);
		lineBuffer.addVertex(x, y + height, 0);
		lineBuffer.addColor(color);

		lineBuffer.addLine(startVertex, startVertex + 1);
		lineBuffer.addLine(startVertex + 1, startVertex + 2);
		lineBuffer.addLine(startVertex + 2, startVertex + 3);
		lineBuffer.addLine(startVertex + 3, startVertex);

		currentVertexIndex += 4;
	}

	public void drawBoxOutline(Rectangle rect, Color color) {
		drawBoxOutline(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), color);
	}

	public void drawLine(float x1, float y1, float x2, float y2, Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		int startVertex = currentVertexIndex;

		lineBuffer.addVertex(x1, y1, 0);
		lineBuffer.addColor(color);
		lineBuffer.addVertex(x2, y2, 0);
		lineBuffer.addColor(color);

		lineBuffer.addLine(startVertex, startVertex + 1);

		currentVertexIndex += 2;
	}

	public void drawRoundedBox(float x, float y, float width, float height, float radius, Color color) {
		if (radius <= 0) {
			drawBox(x, y, width, height, color);
			return;
		}

		drawBox(x + radius, y, width - 2 * radius, height, color);
		drawBox(x, y + radius, radius, height - 2 * radius, color);
		drawBox(x + width - radius, y + radius, radius, height - 2 * radius, color);

		drawFilledArc(x + radius, y + radius, radius, 180, 90, color);
		drawFilledArc(x + width - radius, y + radius, radius, 270, 90, color);
		drawFilledArc(x + width - radius, y + height - radius, radius, 0, 90, color);
		drawFilledArc(x + radius, y + height - radius, radius, 90, 90, color);
	}

	public void drawRoundedBox(Rectangle rect, float radius, Color color) {
		drawRoundedBox(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), radius, color);
	}

	public void drawCircle(float x, float y, float radius, Color color) {
		drawFilledArc(x, y, radius, 0, 360, color);
	}

	private void drawFilledArc(float centerX, float centerY, float radius, float startAngle, float arcAngle,
			Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		int segments = Math.max(8, (int) (arcAngle / 15.0f));
		float angleStep = arcAngle / segments;

		int centerVertex = currentVertexIndex;
		triangleBuffer.addVertex(centerX, centerY, 0);
		triangleBuffer.addColor(color);
		currentVertexIndex++;

		for (int i = 0; i <= segments; i++) {
			float angle = (float) Math.toRadians(startAngle + i * angleStep);
			float px = centerX + radius * (float) Math.cos(angle);
			float py = centerY + radius * (float) Math.sin(angle);

			triangleBuffer.addVertex(px, py, 0);
			triangleBuffer.addColor(color);

			if (i > 0) {
				triangleBuffer.addTriangle(centerVertex, currentVertexIndex - 1, currentVertexIndex);
			}
			currentVertexIndex++;
		}
	}

	public void drawOutlinedBox(float x, float y, float width, float height, Color outlineColor, Color fillColor) {
		drawBox(x, y, width, height, fillColor);
		drawBoxOutline(x, y, width, height, outlineColor);
	}

	public void drawOutlinedBox(Rectangle rect, Color outlineColor, Color fillColor) {
		drawOutlinedBox(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), outlineColor, fillColor);
	}

	public void drawTriangle(float x1, float y1, Color color1, float x2, float y2, Color color2, float x3, float y3,
			Color color3) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		int startVertex = currentVertexIndex;

		triangleBuffer.addVertex(x1, y1, 0);
		triangleBuffer.addColor(color1);
		triangleBuffer.addVertex(x2, y2, 0);
		triangleBuffer.addColor(color2);
		triangleBuffer.addVertex(x3, y3, 0);
		triangleBuffer.addColor(color3);

		triangleBuffer.addTriangle(startVertex, startVertex + 1, startVertex + 2);

		currentVertexIndex += 3;
	}

	public void drawHorizontalGradient(float x, float y, float width, float height, Color startColor, Color endColor) {
		drawTriangle(x, y, startColor, x + width, y, startColor, x, y + height, endColor);
		drawTriangle(x + width, y, startColor, x + width, y + height, endColor, x, y + height, endColor);
	}

	public void drawVerticalGradient(float x, float y, float width, float height, Color startColor, Color endColor) {
		drawTriangle(x, y, startColor, x + width, y, endColor, x, y + height, startColor);
		drawTriangle(x + width, y, endColor, x + width, y + height, endColor, x, y + height, startColor);
	}

	// TODO: Remove for dedicated string renderer
	public void drawString(DrawContext drawContext, String text, float x, float y, Color color) {
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.getMatrices();
		matrixStack.pushMatrix();
		matrixStack.scale(2.0f, 2.0f);
		matrixStack.translate(-x / 2, -y / 2);
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
		matrixStack.popMatrix();
	}

	/**
	 * Draws a string at a certain position.
	 *
	 * @param drawContext Draw context.
	 * @param text        Text to draw on the screen.
	 * @param x           X position to draw the string.
	 * @param y           Y position to draw the string.
	 * @param color       Color (as int) to draw the string.
	 */
	public void drawString(DrawContext drawContext, String text, float x, float y, int color) {
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.getMatrices();
		matrixStack.pushMatrix();
		matrixStack.scale(2.0f, 2.0f);
		matrixStack.translate(-x / 2, -y / 2);
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
		matrixStack.popMatrix();
	}

	/**
	 * Draws a string at a certain position with a scale.
	 *
	 * @param drawContext Draw context.
	 * @param text        Text to draw on the screen.
	 * @param x           X position to draw the string.
	 * @param y           Y position to draw the string.
	 * @param color       Color to draw the string.
	 * @param scale       Scale to draw the string.
	 */
	public void drawStringWithScale(DrawContext drawContext, String text, float x, float y, Color color, float scale) {
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.getMatrices();
		matrixStack.pushMatrix();
		matrixStack.scale(scale, scale);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y);
		}
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
		matrixStack.popMatrix();
	}

	/**
	 * Draws a string at a certain position with a scale.
	 *
	 * @param drawContext Draw context.
	 * @param text        Text to draw on the screen.
	 * @param x           X position to draw the string.
	 * @param y           Y position to draw the string.
	 * @param color       Color (as int) to draw the string.
	 * @param scale       Scale to draw the string.
	 */
	public void drawStringWithScale(DrawContext drawContext, String text, float x, float y, int color, float scale) {
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.getMatrices();
		matrixStack.pushMatrix();
		matrixStack.scale(scale, scale);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y);
		}
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
		matrixStack.popMatrix();
	}

	/**
	 * Wrapper for Minecraft's font renderer getWidth
	 *
	 * @param text Text to get width of.
	 * @return Width of text in pixels.
	 */
	public int getStringWidth(String text) {
		TextRenderer textRenderer = Aoba.getInstance().fontManager.GetRenderer();
		return textRenderer.getWidth(text);
	}

	public void clearStorageFrame() {
		UNIFORM_STORAGE.clear();
	}
}