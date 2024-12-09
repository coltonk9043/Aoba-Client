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

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class DirectLoginAltScreen extends Screen {

	private final Screen parent;
	private ButtonWidget buttonLoginAlt;

	private TextFieldWidget textFieldAltUsername;
	private CheckboxWidget isCracked;
	private boolean didLoginError = false;

	protected DirectLoginAltScreen(Screen parent) {
		super(Text.of("Direct Login"));
		this.parent = parent;
	}

	public void init() {
		super.init();

		this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 50, 200, 20,
				Text.of("Enter Name"));
		this.addDrawableChild(this.textFieldAltUsername);

		this.isCracked = CheckboxWidget.builder(Text.of("Cracked?"), textRenderer)
				.pos(this.width / 2 - 100, height / 2 - 20).maxWidth(200).build();
		this.addDrawableChild(this.isCracked);

		this.buttonLoginAlt = ButtonWidget.builder(Text.of("Login"), b -> this.onButtonLoginPressed())
				.dimensions(this.width / 2 - 100, this.height / 2 + 24, 200, 20).build();
		this.addDrawableChild(this.buttonLoginAlt);

		this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parent))
				.dimensions(this.width / 2 - 100, this.height / 2 + 46, 200, 20).build());
	}

	private void onButtonLoginPressed() {
		boolean loggedIn;
		if (isCracked.isChecked()) {
			Aoba.getInstance().altManager.loginCracked(this.textFieldAltUsername.getText());
			client.setScreen(this.parent);
			return;
		} else {
			Alt alt = new Alt(this.textFieldAltUsername.getText(), false);
			alt.auth();
			loggedIn = Aoba.getInstance().altManager.login(alt);
		}

		if (!loggedIn) {
			didLoginError = true;
		} else {
			client.setScreen(this.parent);
		}
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
		super.render(drawContext, mouseX, mouseY, partialTicks);
		drawContext.drawCenteredTextWithShadow(textRenderer, this.title.getString(), this.width / 2, 20, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Enter Username/Email", this.width / 2 - 100, height / 2 - 60,
				16777215);
		// drawStringWithShadow(matrixStack,textRenderer, "Microsoft: ", this.width / 2
		// - 100, height / 2 - 10, 16777215);
		this.textFieldAltUsername.render(drawContext, mouseX, mouseY, partialTicks);
		if (didLoginError) {
			drawContext.drawTextWithShadow(textRenderer, "Incorrect Login (Try using Email rather than Username)",
					this.width / 2 - 140, 116, 0xFF0000);
		}

	}
}
