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
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.KeyUpEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.event.KeyEvent;

import static net.aoba.AobaClient.MC;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(at = {@At("HEAD")}, method = {"onKey(JIIII)V"}, cancellable = true)
    private void OnKeyDown(long window, int key, int scancode,
                           int action, int modifiers, CallbackInfo ci) {
        AobaClient aoba = Aoba.getInstance();

        if (action == GLFW.GLFW_PRESS) {
            if (aoba != null && aoba.eventManager != null) {
                KeyDownEvent event = new KeyDownEvent(window, key, scancode, action, modifiers);
                
                Aoba.getInstance().eventManager.Fire(event);

                if (event.isCancelled()) {
                    ci.cancel();
                }
            }

            if (MC.currentScreen == null && MC.getOverlay() == null) {
                if (key == KeyEvent.VK_PERIOD) {
                    MC.setScreen(new ChatScreen(""));
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (aoba != null && aoba.eventManager != null) {
                KeyUpEvent event = new KeyUpEvent(window, key, scancode, action, modifiers);

                Aoba.getInstance().eventManager.Fire(event);

                if (event.isCancelled()) {
                    ci.cancel();
                }
            }
        }
    }
}
