/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.alts;

import net.aoba.Aoba;
import net.aoba.managers.altmanager.Alt;
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
		if (isCracked.isChecked()) {
			Aoba.getInstance().altManager.loginCracked(this.textFieldAltUsername.getText());
			client.setScreen(this.parent);
			return;
		} else {
			Alt alt = new Alt(this.textFieldAltUsername.getText(), false);
			alt.auth();
			Aoba.getInstance().altManager.login(alt);
		}
		client.setScreen(this.parent);
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
