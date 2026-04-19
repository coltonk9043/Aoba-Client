package net.aoba.rendering.shaders;

public record ShaderUniform(String name, ShaderUniformType type, float[] defaultValue, float min, float max, float step) {
	public static ShaderUniform ofFloat(String name, float defaultValue, float min, float max, float step) {
		return new ShaderUniform(name, ShaderUniformType.FLOAT, new float[] { defaultValue }, min, max, step);
	}

	public static ShaderUniform ofColor(String name, float r, float g, float b, float a) {
		return new ShaderUniform(name, ShaderUniformType.COLOR, new float[] { r, g, b, a }, 0, 0, 0);
	}

	public static ShaderUniform ofFramebuffer(String name) {
		return new ShaderUniform(name, ShaderUniformType.FRAMEBUFFER, new float[0], 0, 0, 0);
	}

	public static ShaderUniform ofTime() {
		return new ShaderUniform("Time", ShaderUniformType.TIME, new float[0], 0, 0, 0);
	}

	public static ShaderUniform ofResolution() {
		return new ShaderUniform("Resolution", ShaderUniformType.RESOLUTION, new float[0], 0, 0, 0);
	}
}