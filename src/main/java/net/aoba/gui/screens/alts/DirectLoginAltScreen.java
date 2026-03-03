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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DirectLoginAltScreen extends Screen {

	private final Screen parent;
	private Button buttonLoginAlt;

	private EditBox textFieldAltUsername;
	private Checkbox isCracked;
	private final boolean didLoginError = false;

	protected DirectLoginAltScreen(Screen parent) {
		super(Component.nullToEmpty("Direct Login"));
		this.parent = parent;
	}

	public void init() {
		super.init();

		textFieldAltUsername = new EditBox(font, width / 2 - 100, height / 2 - 50, 200, 20,
				Component.nullToEmpty("Enter Name"));
		addRenderableWidget(textFieldAltUsername);

		isCracked = Checkbox.builder(Component.nullToEmpty("Cracked?"), font)
				.pos(width / 2 - 100, height / 2 - 20).maxWidth(200).build();
		addRenderableWidget(isCracked);

		buttonLoginAlt = Button.builder(Component.nullToEmpty("Login"), b -> onButtonLoginPressed())
				.bounds(width / 2 - 100, height / 2 + 24, 200, 20).build();
		addRenderableWidget(buttonLoginAlt);

		addRenderableWidget(Button.builder(Component.nullToEmpty("Cancel"), b -> minecraft.setScreen(parent))
				.bounds(width / 2 - 100, height / 2 + 46, 200, 20).build());
	}

	private void onButtonLoginPressed() {
		if (isCracked.selected()) {
			Aoba.getInstance().altManager.loginCracked(textFieldAltUsername.getValue());
			minecraft.setScreen(parent);
			return;
		} else {
			buttonLoginAlt.active = false;
			Alt alt = new Alt(textFieldAltUsername.getValue(), false);
			Aoba.getInstance().altManager.loginWithAuth(alt, () -> {
				minecraft.execute(() -> minecraft.setScreen(parent));
			});
		}
	}

	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float partialTicks) {
		super.render(drawContext, mouseX, mouseY, partialTicks);
		drawContext.drawCenteredString(font, title.getString(), width / 2, 20, 0xFFFFFFFF);
		drawContext.drawString(font, "Enter Username/Email", width / 2 - 100, height / 2 - 60,
				0xFFFFFFFF);
		textFieldAltUsername.render(drawContext, mouseX, mouseY, partialTicks);
		if (didLoginError) {
			drawContext.drawString(font, "Incorrect Login (Try using Email rather than Username)",
					width / 2 - 140, 116, 0xFFFF0000);
		}

	}
}
