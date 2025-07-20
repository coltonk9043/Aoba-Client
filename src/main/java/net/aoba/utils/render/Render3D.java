package net.aoba.utils.render;

import java.util.OptionalInt;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.aoba.gui.colors.Color;
import net.aoba.utils.render.core.BufferManager;
import net.aoba.utils.render.core.IRenderer;
import net.aoba.utils.render.mesh.UboData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Render3D implements IRenderer {
	private final DynamicUniformStorage<UboData> UNIFORM_STORAGE = new DynamicUniformStorage<>("Aoba 3D UBO",
			new Std140SizeCalculator().putMat4f().putMat4f().get(), 16);
	private final UboData UBO_DATA = new UboData();

	private final BufferManager triangleBuffer;
	private final BufferManager lineBuffer;
	private boolean isBuilding = false;
	private int currentVertexIndex = 0;

	public Render3D() {
		this.triangleBuffer = new BufferManager(28);
		this.lineBuffer = new BufferManager(28);
	}

	@Override
	public void begin() {
		if (isBuilding) {
			throw new IllegalStateException("Renderer is already building");
		}
		isBuilding = true;
		triangleBuffer.clear();
		lineBuffer.clear();
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
		triangleBuffer.clear();
		lineBuffer.clear();
		currentVertexIndex = 0;
	}

	@Override
	public void render() {
		try {
			if (!triangleBuffer.isEmpty()) {
				renderBuffer(triangleBuffer, AobaRenderPipelines.TRIS);
			}
			if (!lineBuffer.isEmpty()) {
				renderBuffer(lineBuffer, AobaRenderPipelines.LINES);
			}
		} finally {
			reset();
		}
	}

	private void renderBuffer(BufferManager buffer, RenderPipeline pipeline) {
		Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();

		GpuTextureView colorAttachment = framebuffer.getColorAttachmentView();
		GpuBuffer vertexBuffer = buffer.createVertexBuffer(VertexFormats.POSITION_COLOR);
		GpuBuffer indexBuffer = buffer.createIndexBuffer(VertexFormats.POSITION_COLOR);

		UBO_DATA.proj = RenderManager.projection;
		UBO_DATA.modelView = RenderSystem.getModelViewMatrix();

		GpuBufferSlice matrixData = UNIFORM_STORAGE.write(UBO_DATA);

		RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Aoba 3D Renderer",
				colorAttachment, OptionalInt.empty());

		pass.setPipeline(pipeline);
		pass.setUniform("Matrices", matrixData);
		pass.setVertexBuffer(0, vertexBuffer);
		pass.setIndexBuffer(indexBuffer, VertexFormat.IndexType.INT);
		pass.drawIndexed(0, 0, buffer.getIndexCount(), 1);
		pass.close();
	}

	public void drawBox(Box box, Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		Box offsetBox = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		int startVertex = currentVertexIndex;

		triangleBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.minY, (float) offsetBox.minZ);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.minY, (float) offsetBox.minZ);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.maxY, (float) offsetBox.minZ);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.maxY, (float) offsetBox.minZ);
		triangleBuffer.addColor(color);

		triangleBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.minY, (float) offsetBox.maxZ);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.minY, (float) offsetBox.maxZ);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.maxY, (float) offsetBox.maxZ);
		triangleBuffer.addColor(color);
		triangleBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.maxY, (float) offsetBox.maxZ);
		triangleBuffer.addColor(color);

		triangleBuffer.addTriangle(startVertex, startVertex + 1, startVertex + 2);
		triangleBuffer.addTriangle(startVertex, startVertex + 2, startVertex + 3);

		triangleBuffer.addTriangle(startVertex + 4, startVertex + 6, startVertex + 5);
		triangleBuffer.addTriangle(startVertex + 4, startVertex + 7, startVertex + 6);

		triangleBuffer.addTriangle(startVertex, startVertex + 4, startVertex + 5);
		triangleBuffer.addTriangle(startVertex, startVertex + 5, startVertex + 1);

		triangleBuffer.addTriangle(startVertex + 2, startVertex + 6, startVertex + 7);
		triangleBuffer.addTriangle(startVertex + 2, startVertex + 7, startVertex + 3);

		triangleBuffer.addTriangle(startVertex, startVertex + 3, startVertex + 7);
		triangleBuffer.addTriangle(startVertex, startVertex + 7, startVertex + 4);

		triangleBuffer.addTriangle(startVertex + 1, startVertex + 5, startVertex + 6);
		triangleBuffer.addTriangle(startVertex + 1, startVertex + 6, startVertex + 2);

		currentVertexIndex += 8;
	}

	public void drawBoxOutline(Box box, Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		Box offsetBox = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		int startVertex = currentVertexIndex;

		lineBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.minY, (float) offsetBox.minZ);
		lineBuffer.addColor(color);
		lineBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.minY, (float) offsetBox.minZ);
		lineBuffer.addColor(color);
		lineBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.maxY, (float) offsetBox.minZ);
		lineBuffer.addColor(color);
		lineBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.maxY, (float) offsetBox.minZ);
		lineBuffer.addColor(color);

		lineBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.minY, (float) offsetBox.maxZ);
		lineBuffer.addColor(color);
		lineBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.minY, (float) offsetBox.maxZ);
		lineBuffer.addColor(color);
		lineBuffer.addVertex((float) offsetBox.maxX, (float) offsetBox.maxY, (float) offsetBox.maxZ);
		lineBuffer.addColor(color);
		lineBuffer.addVertex((float) offsetBox.minX, (float) offsetBox.maxY, (float) offsetBox.maxZ);
		lineBuffer.addColor(color);

		lineBuffer.addLine(startVertex, startVertex + 1);
		lineBuffer.addLine(startVertex + 1, startVertex + 2);
		lineBuffer.addLine(startVertex + 2, startVertex + 3);
		lineBuffer.addLine(startVertex + 3, startVertex);

		lineBuffer.addLine(startVertex + 4, startVertex + 5);
		lineBuffer.addLine(startVertex + 5, startVertex + 6);
		lineBuffer.addLine(startVertex + 6, startVertex + 7);
		lineBuffer.addLine(startVertex + 7, startVertex + 4);

		lineBuffer.addLine(startVertex, startVertex + 4);
		lineBuffer.addLine(startVertex + 1, startVertex + 5);
		lineBuffer.addLine(startVertex + 2, startVertex + 6);
		lineBuffer.addLine(startVertex + 3, startVertex + 7);

		currentVertexIndex += 8;
	}

	public void drawLine(Vec3d start, Vec3d end, Color color) {
		drawLine(start.x, start.y, start.z, end.x, end.y, end.z, color);
	}

	public void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		int startVertex = currentVertexIndex;

		lineBuffer.addVertex((float) (x1 - cameraPos.x), (float) (y1 - cameraPos.y), (float) (z1 - cameraPos.z));
		lineBuffer.addColor(color);
		lineBuffer.addVertex((float) (x2 - cameraPos.x), (float) (y2 - cameraPos.y), (float) (z2 - cameraPos.z));
		lineBuffer.addColor(color);

		lineBuffer.addLine(startVertex, startVertex + 1);

		currentVertexIndex += 2;
	}

	public void drawSphere(Vec3d center, float radius, Color color) {
		drawSphere(center.x, center.y, center.z, radius, color);
	}

	public void drawSphere(double x, double y, double z, float radius, Color color) {
		if (!isBuilding) {
			throw new IllegalStateException("Must call begin() before drawing");
		}

		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		int rings = 16;
		int sectors = 16;

		float centerX = (float) (x - cameraPos.x);
		float centerY = (float) (y - cameraPos.y);
		float centerZ = (float) (z - cameraPos.z);

		int startVertex = currentVertexIndex;

		for (int r = 0; r <= rings; r++) {
			float lat = (float) (Math.PI * r / rings - Math.PI / 2);
			float y1 = (float) Math.sin(lat) * radius;
			float ringRadius = (float) Math.cos(lat) * radius;

			for (int s = 0; s <= sectors; s++) {
				float lng = (float) (2 * Math.PI * s / sectors);
				float x1 = (float) Math.cos(lng) * ringRadius;
				float z1 = (float) Math.sin(lng) * ringRadius;

				triangleBuffer.addVertex(centerX + x1, centerY + y1, centerZ + z1);
				triangleBuffer.addColor(color);
				currentVertexIndex++;

				if (r < rings && s < sectors) {
					int current = startVertex + r * (sectors + 1) + s;
					int next = current + sectors + 1;

					triangleBuffer.addTriangle(current, next, current + 1);
					triangleBuffer.addTriangle(current + 1, next, next + 1);
				}
			}
		}
	}

	public void clearStorageFrame() {
		UNIFORM_STORAGE.clear();
	}

	/**
	 * Gets the interpolated position of the entity given a tick delta.
	 *
	 * @param entity Entity to get position of
	 * @param delta  Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3d getEntityPositionInterpolated(Entity entity, float delta) {
		return new Vec3d(MathHelper.lerp(delta, entity.lastX, entity.getX()),
				MathHelper.lerp(delta, entity.lastY, entity.getY()),
				MathHelper.lerp(delta, entity.lastZ, entity.getZ()));
	}

	/**
	 * Gets the difference between the interpolated position and
	 *
	 * @param entity Entity to get position of
	 * @param delta  Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3d getEntityPositionOffsetInterpolated(Entity entity, float delta) {
		Vec3d interpolated = getEntityPositionInterpolated(entity, delta);
		return entity.getPos().subtract(interpolated);
	}
}