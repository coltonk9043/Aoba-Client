package net.aoba.rendering.shaders;

public enum ShaderUniformType {
	FLOAT(1),
	COLOR(4),
	FRAMEBUFFER(0),
	TIME(0),
	RESOLUTION(0);

	public final int size;

	ShaderUniformType(int size) {
		this.size = size;
	}
}