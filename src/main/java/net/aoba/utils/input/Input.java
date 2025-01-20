/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

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
