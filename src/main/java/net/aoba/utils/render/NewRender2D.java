package net.aoba.utils.render;

import java.util.OptionalInt;

import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.core.BufferManager;
import net.aoba.utils.render.core.IRenderer;
import net.aoba.utils.render.core.RenderContext;
import net.aoba.utils.render.mesh.UboData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gl.Framebuffer;

public class NewRender2D implements IRenderer {
    private static final DynamicUniformStorage<UboData> UNIFORM_STORAGE = new DynamicUniformStorage<>("Aoba 2D UBO", 
        net.aoba.utils.render.mesh.MeshRenderer.SIZE, 16);
    private static final UboData UBO_DATA = new UboData();
    
    private final BufferManager triangleBuffer;
    private final BufferManager lineBuffer;
    private boolean isBuilding = false;
    private int currentVertexIndex = 0;
    
    public NewRender2D() {
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
    public void render(DrawContext context) {
        System.out.println("Render called - triangles: v=" + triangleBuffer.getVertexCount() + " i=" + triangleBuffer.getIndexCount() + ", lines: v=" + lineBuffer.getVertexCount() + " i=" + lineBuffer.getIndexCount());
        try {
            if (triangleBuffer.getVertexCount() > 0 && triangleBuffer.getIndexCount() > 0) {
                renderBuffer(triangleBuffer, AobaRenderPipelines.TRIS_GUI, context);
            }
            if (lineBuffer.getVertexCount() > 0 && lineBuffer.getIndexCount() > 0) {
                renderBuffer(lineBuffer, AobaRenderPipelines.LINES_GUI, context);
            }
        } finally {
            triangleBuffer.resetAfterRender();
            lineBuffer.resetAfterRender();
        }
    }
    
    private void renderBuffer(BufferManager buffer, RenderPipeline pipeline, DrawContext context) {
        if (buffer.getVertexCount() == 0 || buffer.getIndexCount() == 0) {
            return;
        }
        
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        
        GpuTextureView colorAttachment = framebuffer.getColorAttachmentView();
        GpuBuffer vertexBuffer = buffer.createVertexBuffer(VertexFormats.POSITION_COLOR);
        GpuBuffer indexBuffer = buffer.createIndexBuffer(VertexFormats.POSITION_COLOR);
        
        UBO_DATA.proj = net.aoba.utils.render.mesh.MeshRenderer.projection;
        UBO_DATA.modelView = RenderSystem.getModelViewMatrix();
        
        GpuBufferSlice matrixData = UNIFORM_STORAGE.write(UBO_DATA);
        
        RenderPass pass = RenderSystem.getDevice().createCommandEncoder()
            .createRenderPass(() -> "Aoba 2D Renderer", colorAttachment, OptionalInt.empty());
        
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
        
        System.out.println("Drew box: startVertex=" + startVertex + ", triangles: [" + startVertex + "," + (startVertex + 1) + "," + (startVertex + 2) + "] [" + startVertex + "," + (startVertex + 2) + "," + (startVertex + 3) + "]");
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
    
    private void drawFilledArc(float centerX, float centerY, float radius, float startAngle, float arcAngle, Color color) {
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
    
    public void drawTriangle(float x1, float y1, Color color1, float x2, float y2, Color color2, float x3, float y3, Color color3) {
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
    
    public static void clearStorageFrame() {
        UNIFORM_STORAGE.clear();
    }
}