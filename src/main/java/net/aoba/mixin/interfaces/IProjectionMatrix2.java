package net.aoba.mixin.interfaces;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.ProjectionMatrix2;

@Mixin(ProjectionMatrix2.class)
public interface IProjectionMatrix2 {
	@Invoker("getMatrix")
	Matrix4f executeGetMatrix(float width, float height);
}
