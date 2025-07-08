package net.aoba.utils.render.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.aoba.gui.colors.Color;

public class BufferManager {
    private static final int INITIAL_VERTEX_CAPACITY = 1024;
    private static final int INITIAL_INDEX_CAPACITY = 1024;
    
    private ByteBuffer vertexBuffer;
    private IntBuffer indexBuffer;
    private int vertexCount;
    private int indexCount;
    private final int vertexSize;
    
    public BufferManager(int vertexSize) {
        this.vertexSize = vertexSize;
        this.vertexBuffer = BufferUtils.createByteBuffer(INITIAL_VERTEX_CAPACITY * vertexSize);
        this.indexBuffer = BufferUtils.createIntBuffer(INITIAL_INDEX_CAPACITY);
        this.vertexCount = 0;
        this.indexCount = 0;
    }
    
    public void clear() {
        vertexBuffer.clear();
        indexBuffer.clear();
        vertexCount = 0;
        indexCount = 0;
    }
    
    public void ensureVertexCapacity(int additionalVertices) {
        int totalVertices = vertexCount + additionalVertices;
        int requiredBytes = totalVertices * vertexSize;
        
        if (requiredBytes > vertexBuffer.capacity()) {
            int newCapacity = Math.max(vertexBuffer.capacity() * 2, requiredBytes);
            ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
            vertexBuffer.flip();
            newBuffer.put(vertexBuffer);
            vertexBuffer = newBuffer;
        }
    }
    
    public void ensureIndexCapacity(int additionalIndices) {
        int totalIndices = indexCount + additionalIndices;
        
        if (totalIndices > indexBuffer.capacity()) {
            int newCapacity = Math.max(indexBuffer.capacity() * 2, totalIndices);
            IntBuffer newBuffer = BufferUtils.createIntBuffer(newCapacity);
            indexBuffer.flip();
            newBuffer.put(indexBuffer);
            indexBuffer = newBuffer;
        }
    }
    
    public void addVertex(float x, float y, float z) {
        ensureVertexCapacity(1);
        vertexBuffer.putFloat(x);
        vertexBuffer.putFloat(y);
        vertexBuffer.putFloat(z);
        vertexCount++;
    }
    
    public void addColor(Color color) {
        vertexBuffer.putFloat(color.r / 255.0f);
        vertexBuffer.putFloat(color.g / 255.0f);
        vertexBuffer.putFloat(color.b / 255.0f);
        vertexBuffer.putFloat(color.a / 255.0f);
    }
    
    public void addIndex(int index) {
        ensureIndexCapacity(1);
        indexBuffer.put(index);
        indexCount++;
    }
    
    public void addTriangle(int v1, int v2, int v3) {
        ensureIndexCapacity(3);
        indexBuffer.put(v1);
        indexBuffer.put(v2);
        indexBuffer.put(v3);
        indexCount += 3;
    }
    
    public void addLine(int v1, int v2) {
        ensureIndexCapacity(2);
        indexBuffer.put(v1);
        indexBuffer.put(v2);
        indexCount += 2;
    }
    
    public GpuBuffer createVertexBuffer(VertexFormat vertexFormat) {
        if (vertexCount == 0) {
            throw new IllegalStateException("Cannot create vertex buffer with no vertices");
        }
        
        ByteBuffer vertexData = BufferUtils.createByteBuffer(vertexCount * vertexSize);
        vertexBuffer.flip();
        if (vertexBuffer.remaining() == 0) {
            throw new IllegalStateException("Vertex buffer is empty after flip, vertexCount=" + vertexCount);
        }
        vertexData.put(vertexBuffer);
        vertexData.flip();
        
        System.out.println("Vertex buffer created: vertexCount=" + vertexCount + ", vertexSize=" + vertexSize + ", buffer.remaining=" + vertexData.remaining());
        
        return vertexFormat.uploadImmediateVertexBuffer(vertexData);
    }
    
    public GpuBuffer createIndexBuffer(VertexFormat vertexFormat) {
        if (indexCount == 0) {
            throw new IllegalStateException("Cannot create index buffer with no indices");
        }
        
        ByteBuffer indexByteBuffer = BufferUtils.createByteBuffer(indexCount * 4);
        indexBuffer.flip();
        
        if (indexBuffer.remaining() == 0) {
            throw new IllegalStateException("Index buffer is empty after flip, indexCount=" + indexCount);
        }
        
        IntBuffer intView = indexByteBuffer.asIntBuffer();
        intView.put(indexBuffer);
        
        indexByteBuffer.position(0);
        indexByteBuffer.limit(indexCount * 4);
        
        System.out.println("Index buffer created: indexCount=" + indexCount + ", buffer.remaining=" + indexByteBuffer.remaining());
        
        return vertexFormat.uploadImmediateIndexBuffer(indexByteBuffer);
    }
    
    public int getVertexCount() { return vertexCount; }
    public int getIndexCount() { return indexCount; }
    public boolean isEmpty() { return indexCount == 0 || vertexCount == 0; }
    
    public void resetAfterRender() {
        vertexBuffer.clear();
        indexBuffer.clear();
        vertexCount = 0;
        indexCount = 0;
    }
}