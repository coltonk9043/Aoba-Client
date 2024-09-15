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

package net.aoba.gui.screens.alts;

import com.mojang.logging.LogUtils;
import net.aoba.Aoba;
import net.aoba.altmanager.exceptions.APIDownException;
import net.aoba.altmanager.exceptions.APIErrorException;
import net.aoba.altmanager.exceptions.InvalidResponseException;
import net.aoba.altmanager.exceptions.InvalidTokenException;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class MCLeaksLoginScreen extends Screen {

    private final Screen parent;
    private ButtonWidget buttonLoginAlt;

    private TextFieldWidget textFieldToken;

    private boolean didLoginError = false;

    protected MCLeaksLoginScreen(Screen parent) {
        super(Text.of("MCLeaks Login"));
        this.parent = parent;
    }

    public void init() {
        super.init();

        this.textFieldToken = new TextFieldWidget(textRenderer, this.width / 2 - 100, 206, 200, 20, Text.of("Enter MCLeaks Token"));
        this.addDrawableChild(this.textFieldToken);


        this.buttonLoginAlt = ButtonWidget.builder(Text.of("Login"), b -> this.onButtonLoginPressed())
                .dimensions(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20).build();
        this.addDrawableChild(this.buttonLoginAlt);
        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parent))
                .dimensions(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20).build());
    }

    private void onButtonLoginPressed() {
		/*
		 * try {
		 * Aoba.getInstance().altManager.loginMCLeaks(this.textFieldToken.getText());
		 * client.setScreen(this.parent); } catch (APIDownException | APIErrorException
		 * | InvalidResponseException | InvalidTokenException e) { didLoginError = true;
		 * LogUtils.getLogger().error(e.getMessage()); }
		 */
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    	super.render(drawContext, mouseX, mouseY, partialTicks);
        drawContext.drawCenteredTextWithShadow(textRenderer, this.title.getString(), this.width / 2, 17, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "Enter Token", this.width / 2 - 100, 154, 10526880);
        this.textFieldToken.render(drawContext, mouseX, mouseY, partialTicks);
        if (didLoginError) {
            drawContext.drawTextWithShadow(textRenderer, "Incorrect Token", this.width / 2 - 140, 116, 0xFF0000);
        }
    }
}