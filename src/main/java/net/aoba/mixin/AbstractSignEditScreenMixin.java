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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.managers.CommandManager;
import net.aoba.module.modules.world.AutoSign;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen {
	@Shadow
	@Final
	private String[] messages;

	protected AbstractSignEditScreenMixin(Text title) {
		super(title);
	}

	@Inject(at = { @At("HEAD") }, method = { "init()V" })
	private void onInit(CallbackInfo ci) {
		AutoSign mod = Aoba.getInstance().moduleManager.autosign;
		String[] newText = mod.getText();
		if (newText != null) {
			System.arraycopy(newText, 0, messages, 0, 4);
			finishEditing();
		}
	}

	@Inject(at = { @At("HEAD") }, method = "finishEditing()V")
	private void onEditorClose(CallbackInfo ci) {
		AutoSign mod = Aoba.getInstance().moduleManager.autosign;
		if (mod.state.getValue()) {
			if (mod.getText() == null) {
				mod.setText(messages);
				CommandManager.sendChatMessage("Sign text set!");
			}
		}
	}

	@Shadow
	private void finishEditing() {

	}
}
