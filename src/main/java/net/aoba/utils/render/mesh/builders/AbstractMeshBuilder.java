package net.aoba.utils.render.mesh.builders;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

import net.aoba.gui.colors.Color;
import net.minecraft.util.math.Vec2f;

public class AbstractMeshBuilder {
	protected static final int DEFAULT_VERTICE_COUNT = 512;

	protected final VertexFormat vertexFormat;
	protected final DrawMode drawMode;

	protected ByteBuffer vertices;
	protected ByteBuffer indices;

	protected int lastVerticeIndex;
	protected int verticeCount, indicesCount;

	protected final int primitiveVerticesSize;
	protected final int primitiveIndicesCount;

	protected boolean isBuilding = false;

	public AbstractMeshBuilder(RenderPipeline pipeline) {

		vertexFormat = pipeline.getVertexFormat();
		drawMode = pipeline.getVertexFormatMode();

		primitiveVerticesSize = vertexFormat.getVertexSize();
		primitiveIndicesCount = drawMode.firstVertexCount;

		vertices = BufferUtils.createByteBuffer(primitiveVerticesSize * DEFAULT_VERTICE_COUNT);
		indices = BufferUtils.createByteBuffer(Integer.BYTES * DEFAULT_VERTICE_COUNT);
	}

	public void begin() {
		if (isBuilding) {
			throw new IllegalStateException("MeshBuilder has already begun.");
		}

		isBuilding = true;
		vertices.clear();
		indices.clear();
		lastVerticeIndex = 0;
		verticeCount = 0;
		indicesCount = 0;
	}

	public void end() {
		if (!isBuilding)
			throw new IllegalStateException("MeshBuilder is currently not building.");

		isBuilding = false;
	}

	public AbstractMeshBuilder color(Color color) {
		ensureVerticeCapacity(1 * Float.BYTES);
		vertices.put((byte) color.r);
		vertices.put((byte) color.g);
		vertices.put((byte) color.b);
		vertices.put((byte) color.a);

		verticeCount += 1;
		return this;
	}

	public AbstractMeshBuilder vec3d(double x, double y, double z) {
		ensureVerticeCapacity(3 * Float.BYTES);
		vertices.putFloat((float) x);
		vertices.putFloat((float) y);
		vertices.putFloat((float) z);

		verticeCount += 3;
		return this;
	}

	public AbstractMeshBuilder vec2d(Vec2f vec) {
		ensureVerticeCapacity(3 * Float.BYTES);
		vertices.putFloat(vec.x);
		vertices.putFloat(vec.y);
		vertices.putFloat(0);

		verticeCount += 3;
		return this;
	}

	public AbstractMeshBuilder vec2d(double x, double y) {
		ensureVerticeCapacity(3 * Float.BYTES);
		vertices.putFloat((float) x);
		vertices.putFloat((float) y);
		vertices.putFloat(0);

		verticeCount += 3;
		return this;
	}

	public int next() {
		return lastVerticeIndex++;
	}

	public boolean isEmpty() {
		return verticeCount == 0 || indicesCount == 0;
	}

	public int getVerticesCount() {
		return verticeCount;
	}

	public int getIndicesCount() {
		return indicesCount;
	}

	public void ensureVerticeCapacity(int additionalCapacity) {
		if (additionalCapacity < 0)
			throw new IllegalArgumentException("");

		int sizeNeeded = (verticeCount * Float.BYTES) + additionalCapacity;
		if (sizeNeeded > vertices.capacity()) {
			int newSize = Math.max(Math.max(8, sizeNeeded), vertices.capacity() * 2);
			ByteBuffer newVertices = BufferUtils.createByteBuffer(newSize);
			newVertices.put(vertices.limit(verticeCount * Float.BYTES).rewind());
			vertices = newVertices;
		}
	}

	public void ensureIndiceCapacity(int additionalCapacity) {
		if (additionalCapacity < 0)
			throw new IllegalArgumentException("");

		int sizeNeeded = (indicesCount * Integer.BYTES) + additionalCapacity;
		if (sizeNeeded > indices.capacity()) {
			int newSize = Math.max(Math.max(8, sizeNeeded), indices.capacity() * 2);
			ByteBuffer newIndices = BufferUtils.createByteBuffer(newSize);
			newIndices.put(indices.limit(indicesCount * Integer.BYTES).rewind());
			indices = newIndices;
		}
	}

	public GpuBuffer getGpuVertexBuffer() {
		vertices.limit(verticeCount * Float.BYTES).rewind();
		return vertexFormat.uploadImmediateVertexBuffer(vertices);
	}

	public GpuBuffer getGpuIndiceBuffer() {
		indices.limit(indicesCount * Integer.BYTES).rewind();
		return vertexFormat.uploadImmediateIndexBuffer(indices);
	}
}
