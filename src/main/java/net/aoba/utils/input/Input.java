package net.aoba.utils.input;

import static net.aoba.AobaClient.MC;

import org.lwjgl.glfw.GLFW;

public class Input {
	private static CursorStyle lastCursorStyle = CursorStyle.Default;

	public static void setCursorStyle(CursorStyle style) {

		if (lastCursorStyle != style) {
			GLFW.glfwSetCursor(MC.getWindow().getHandle(), style.getGlfwCursor());
			lastCursorStyle = style;
		}
	}
}
