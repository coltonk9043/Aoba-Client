package net.aoba.rendering;

/**
 * Represents a set of known / implemented Render APIS
 * for use with the rendering pipelines. Common render apis 
 * are OpenGL, Vulkan, etc...
 */
public enum RenderApi {
	OPENGL,
	VULKAN;

	/** Returns the active rendering backend. */
	public static RenderApi current() {
		// TODO: Detects Vulkan in Snapshot 26.2+
		return OPENGL;
	}
}
