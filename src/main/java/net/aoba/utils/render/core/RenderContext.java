package net.aoba.utils.render.core;

import org.joml.Matrix4f;
import org.joml.Matrix3x2fStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

public class RenderContext {
    private final DrawContext drawContext;
    private final Matrix3x2fStack matrixStack;
    private final Matrix4f projection;
    private final Matrix4f modelView;
    private final Camera camera;
    private final float tickDelta;
    private final int screenWidth;
    private final int screenHeight;
    
    public RenderContext(DrawContext drawContext, float tickDelta) {
        this.drawContext = drawContext;
        this.matrixStack = drawContext.getMatrices();
        this.projection = new Matrix4f();
        this.modelView = new Matrix4f();
        this.camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        this.tickDelta = tickDelta;
        this.screenWidth = drawContext.getScaledWindowWidth();
        this.screenHeight = drawContext.getScaledWindowHeight();
    }
    
    public DrawContext getDrawContext() { return drawContext; }
    public Matrix3x2fStack getMatrixStack() { return matrixStack; }
    public Matrix4f getProjection() { return projection; }
    public Matrix4f getModelView() { return modelView; }
    public Camera getCamera() { return camera; }
    public float getTickDelta() { return tickDelta; }
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}