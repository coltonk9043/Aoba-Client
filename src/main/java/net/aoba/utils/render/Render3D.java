/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A class that contains all of the drawing functions.
 */
package net.aoba.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.aoba.gui.colors.Color;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Render3D {
    public static void draw3DBox(MatrixStack matrixStack, Box box, Color color, float lineThickness) {
        RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());

        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();

        Tessellator tessellator = RenderSystem.renderThreadTesselator();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
                color.getAlphaFloat());

        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ);

        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ);

        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ);

        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ);

        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ);

        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ);
        bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(lineThickness);


        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.minY, (float) box.minZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.minY, (float) box.maxZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.maxZ, (float) box.minX, (float) box.minY, (float) box.maxZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.maxZ, (float) box.minX, (float) box.minY, (float) box.minZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.minZ, (float) box.minX, (float) box.maxY, (float) box.minZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.minZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.maxZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.maxZ, (float) box.minX, (float) box.maxY, (float) box.maxZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.maxY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.minZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.maxY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.maxY, (float) box.maxZ, (float) box.minX, (float) box.maxY, (float) box.maxZ, color);
        buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.maxY, (float) box.maxZ, (float) box.minX, (float) box.maxY, (float) box.minZ, color);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.enableCull();
        RenderSystem.lineWidth(1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawLine3D(MatrixStack matrixStack, Vec3d pos1, Vec3d pos2, Color color, float lineWidth) {
        drawLine3D(matrixStack, (float) pos1.x, (float) pos1.y, (float) pos1.z, (float) pos2.x, (float) pos2.y, (float) pos2.z, color, lineWidth);
    }

    public static void drawLine3D(MatrixStack matrixStack, float x1, float y1, float z1, float x2, float y2, float z2, Color color, float lineWidth) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(lineWidth);

        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        buildLine3d(matrixStack, bufferBuilder, x1, y1, z1, x2, y2, z2, color);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.enableCull();
        RenderSystem.lineWidth(1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void buildLine3d(MatrixStack matrixStack, BufferBuilder bufferBuilder, float x1, float y1, float z1, float x2, float y2, float z2, Color color) {
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();

        Vec3d normalized = new Vec3d(x2 - x1, y2 - y1, z2 - z1).normalize();

        float r = color.getRedFloat();
        float g = color.getGreenFloat();
        float b = color.getBlueFloat();

        bufferBuilder.vertex(matrix4f, x1, y1, z1).color(r, g, b, 1.0f).normal(entry, (float) normalized.x, (float) normalized.y, (float) normalized.z);
        bufferBuilder.vertex(matrix4f, x2, y2, z2).color(r, g, b, 1.0f).normal(entry, (float) normalized.x, (float) normalized.y, (float) normalized.z);
    }

    /**
     * Gets the interpolated position of the entity given a tick delta.
     *
     * @param entity Entity to get position of
     * @param delta  Tick delta.
     * @return Vec3d representing the interpolated position of the entity.
     */
    public static Vec3d getEntityPositionInterpolated(Entity entity, float delta) {
        return new Vec3d(MathHelper.lerp(delta, entity.prevX, entity.getX()),
                MathHelper.lerp(delta, entity.prevY, entity.getY()),
                MathHelper.lerp(delta, entity.prevZ, entity.getZ()));
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
