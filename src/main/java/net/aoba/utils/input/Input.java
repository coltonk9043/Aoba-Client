package net.aoba.utils.input;

import org.lwjgl.glfw.GLFW;

import static net.aoba.AobaClient.MC;

public class Input {
    private static CursorStyle lastCursorStyle = CursorStyle.Default;

    public static void setCursorStyle(CursorStyle style) {

        if (lastCursorStyle != style) {
            GLFW.glfwSetCursor(MC.getWindow().getHandle(), style.getGlfwCursor());
            lastCursorStyle = style;
        }
    }
}
