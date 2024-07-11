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

package net.aoba.mixin;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    private double x;
    @Shadow
    private double y;
    
    @Inject(at = {@At("HEAD")}, method = {"onMouseButton(JIII)V"}, cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        AobaClient aoba = Aoba.getInstance();
        if (aoba != null && aoba.eventManager != null) {
            MouseClickEvent event = null;

            switch (button) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                    if (action == 1) {
                        event = new MouseClickEvent(x, y, MouseButton.LEFT, MouseAction.DOWN);
                    } else {
                        event = new MouseClickEvent(x, y, MouseButton.LEFT, MouseAction.UP);
                    }
                    break;
                case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
                    if (action == 1) {
                        event = new MouseClickEvent(x, y, MouseButton.MIDDLE, MouseAction.DOWN);
                    } else {
                        event = new MouseClickEvent(x, y, MouseButton.MIDDLE, MouseAction.UP);
                    }
                    break;
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                    if (action == 1) {
                        event = new MouseClickEvent(x, y, MouseButton.RIGHT, MouseAction.DOWN);
                    } else {
                        event = new MouseClickEvent(x, y, MouseButton.RIGHT, MouseAction.UP);
                    }
                    break;
            }

            aoba.eventManager.Fire(event);

            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"onMouseScroll(JDD)V"}, cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        AobaClient aoba = Aoba.getInstance();
        if (aoba != null && aoba.eventManager != null) {
            MouseScrollEvent event = new MouseScrollEvent(horizontal, vertical);
            aoba.eventManager.Fire(event);

            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"lockCursor()V"}, cancellable = true)
    private void onLockCursor(CallbackInfo ci) {
        if (Aoba.getInstance().hudManager.isClickGuiOpen())
            ci.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"onCursorPos(JDD)V"}, cancellable = true)
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        AobaClient aoba = Aoba.getInstance();
        if (aoba != null && aoba.eventManager != null) {
        	double cursorDeltaX = x - this.x;
        	double cursorDeltaY = y - this.y;
        
            MouseMoveEvent event = new MouseMoveEvent(x, y, cursorDeltaX, cursorDeltaY);
            aoba.eventManager.Fire(event);

            if (event.isCancelled())
                ci.cancel();
        }
    }
}