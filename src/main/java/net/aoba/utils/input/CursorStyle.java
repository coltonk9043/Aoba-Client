package net.aoba.utils.input;

import org.lwjgl.glfw.GLFW;

public enum CursorStyle {
    Default,
    Click,
    Type,
	HorizonalResize,
	VerticalResize;

    private boolean created;
    private long cursor;

    public long getGlfwCursor() {
        if (!created) {
            switch (this) {
                case Click -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
                case Type -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
                case HorizonalResize -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
                case VerticalResize -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
                case Default -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
            }

            created = true;
        }

        return cursor;
    }
}
