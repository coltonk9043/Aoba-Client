package net.aoba.utils.render.mesh;

import java.nio.ByteBuffer;

import org.joml.Matrix4f;

import com.mojang.blaze3d.buffers.Std140Builder;

import net.minecraft.client.gl.DynamicUniformStorage;

public class UboData implements DynamicUniformStorage.Uploadable {
	public Matrix4f proj;
	public Matrix4f modelView;

	@Override
	public void write(ByteBuffer buffer) {
		Std140Builder.intoBuffer(buffer).putMat4f(proj).putMat4f(modelView);
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}
}