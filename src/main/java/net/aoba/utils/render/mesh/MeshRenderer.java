package net.aoba.utils.render.mesh;

import java.util.OptionalInt;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.aoba.utils.render.mesh.builders.AbstractMeshBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.Vec3d;

public class MeshRenderer {
	private static final MeshRenderer INSTANCE = new MeshRenderer();

	public static final int SIZE = new Std140SizeCalculator().putMat4f().putMat4f().get();
	private static final DynamicUniformStorage<UboData> STORAGE = new DynamicUniformStorage<>("Aoba UBO", SIZE, 16);
	private static final UboData UBO_DATA = new UboData();

	public static final Matrix4f projection = new Matrix4f();
	public static final Matrix4f view = new Matrix4f();
	public static Vec3d center;

	private static boolean taken;
	private AbstractMeshBuilder mesh;
	private RenderPipeline pipeline;
	private Matrix4f matrix;
	private Framebuffer framebuffer;

	public static void updateRenderProperties(Matrix4f proj, Matrix4f view) {
		projection.set(proj);

		Matrix4f invProjection = new Matrix4f(projection).invert();
		Matrix4f invView = new Matrix4f(view).invert();

		Vector4f center4 = new Vector4f(0, 0, 0, 1).mul(invProjection).mul(invView);
		center4.div(center4.w);

		Vec3d camera = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
		center = new Vec3d(camera.x + center4.x, camera.y + center4.y, camera.z + center4.z);
	}

	public static MeshRenderer begin() {
		if (taken)
			throw new IllegalStateException("Previous instance of MeshRenderer was not ended");

		taken = true;
		return INSTANCE;
	}

	public static void flipFrame() {
		STORAGE.clear();
	}

	public MeshRenderer withMesh(AbstractMeshBuilder mesh) {
		this.mesh = mesh;
		return this;
	}

	public MeshRenderer withPipeline(RenderPipeline pipeline) {
		this.pipeline = pipeline;
		return this;
	}

	public MeshRenderer withMatrix(Matrix4f matrix) {
		this.matrix = matrix;
		return this;
	}

	public MeshRenderer withFramebuffer(Framebuffer frameBuffer) {
		this.framebuffer = frameBuffer;
		return this;
	}

	public void end() {
		if (!mesh.isEmpty()) {
			if (matrix != null) {
				RenderSystem.getModelViewStack().pushMatrix();
				RenderSystem.getModelViewStack().mul(matrix);
			}

			GpuTextureView colorAttachment = framebuffer.getColorAttachmentView();
			GpuTextureView depthAttachment = framebuffer.getDepthAttachmentView();

			GpuBuffer vertexBuffer = mesh.getGpuVertexBuffer();
			GpuBuffer indexBuffer = mesh.getGpuIndiceBuffer();

			UBO_DATA.proj = projection;
			UBO_DATA.modelView = RenderSystem.getModelViewMatrix();

			GpuBufferSlice matrixData = STORAGE.write(UBO_DATA);

			RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Aoba Renderer",
					colorAttachment, OptionalInt.empty());

			pass.setPipeline(pipeline);
			pass.setUniform("Matrices", matrixData);
			pass.setVertexBuffer(0, vertexBuffer);
			pass.setIndexBuffer(indexBuffer, VertexFormat.IndexType.INT);
			pass.drawIndexed(0, 0, mesh.getIndicesCount(), 1);
			pass.close();

			if (matrix != null) {
				RenderSystem.getModelViewStack().popMatrix();
			}
		}

		framebuffer = null;
		pipeline = null;
		mesh = null;
		matrix = null;
		taken = false;
	}

}
