/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.proxy;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.managers.proxymanager.Socks5Proxy;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AddProxyScreen extends Screen {
	private final ProxyScreen parent;

	private ButtonWidget buttonAddProxy;
	private TextFieldWidget textFieldProxyIp;
	private TextFieldWidget textFieldProxyPort;
	private TextFieldWidget textFieldProxyUsername;
	private TextFieldWidget textFieldProxyPassword;

	public AddProxyScreen(ProxyScreen parentScreen) {
		super(Text.of("Proxy Manager"));
		parent = parentScreen;
	}

	protected void init() {
		super.init();

		textFieldProxyIp = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 76, 200, 20,
				Text.of("Enter IP"));
		textFieldProxyIp.setText("");
		addDrawableChild(textFieldProxyIp);

		textFieldProxyPort = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 36, 200, 20,
				Text.of("Enter Port"));
		textFieldProxyPort.setText("");
		addDrawableChild(textFieldProxyPort);

		textFieldProxyUsername = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 + 4, 200, 20,
				Text.of("Enter Username"));
		textFieldProxyUsername.setText("");
		addDrawableChild(textFieldProxyUsername);

		textFieldProxyPassword = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 + 44, 200, 20,
				Text.of("Enter Password"));
		textFieldProxyPassword.setText("");
		addDrawableChild(textFieldProxyPassword);

		buttonAddProxy = ButtonWidget.builder(Text.of("Add Proxy"), b -> onAddProxyButtonPressed())
				.dimensions(width / 2 - 100, height / 2 + 74, 200, 20).build();
		addDrawableChild(buttonAddProxy);

		addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> onButtonCancelPressed())
				.dimensions(width / 2 - 100, height / 2 + 98, 200, 20).build());
	}

	private void onAddProxyButtonPressed() {
		String ip = textFieldProxyIp.getText();
		String portText = textFieldProxyPort.getText();
		String username = textFieldProxyUsername.getText();
		String password = textFieldProxyPassword.getText();

		if (ip.isEmpty() || portText.isEmpty() || username.isEmpty() || password.isEmpty())
			return;

		try {
			int port = Integer.parseInt(portText);
			Socks5Proxy newProxy = new Socks5Proxy(ip, port, username, password);
			Aoba.getInstance().proxyManager.addProxy(newProxy);
			parent.refreshProxyList();
		} catch (NumberFormatException e) {
			AobaClient.LOGGER.error(e.getMessage());
		}
	}

	public void onButtonCancelPressed() {
		client.setScreen(parent);
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		super.render(drawContext, mouseX, mouseY, delta);
		drawContext.drawCenteredTextWithShadow(textRenderer, "Add Proxy", width / 2, 20, 16777215);

		drawContext.drawTextWithShadow(textRenderer, "IP Address:", width / 2 - 100, height / 2 - 90, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Port:", width / 2 - 100, height / 2 - 50, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Username:", width / 2 - 100, height / 2 - 10, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Password:", width / 2 - 100, height / 2 + 30, 16777215);
	}
}
