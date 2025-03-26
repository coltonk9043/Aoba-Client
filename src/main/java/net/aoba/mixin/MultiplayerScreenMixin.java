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

import net.aoba.gui.screens.alts.AltScreen;
import net.aoba.gui.screens.proxy.ProxyScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = {@At("TAIL")}, method = {"init()V"})
    private void onInit(CallbackInfo ci) {
        addDrawableChild(ButtonWidget.builder(Text.of("Alt Manager"), b -> client.setScreen(new AltScreen((MultiplayerScreen) (Object) this)))
                .dimensions(width / 2 + 69, 5, 85, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.of("Proxy Manager"), b -> client.setScreen(new ProxyScreen((MultiplayerScreen) (Object) this)))
                .dimensions(width / 2 - 154, 5, 85, 20).build());
    }

}
