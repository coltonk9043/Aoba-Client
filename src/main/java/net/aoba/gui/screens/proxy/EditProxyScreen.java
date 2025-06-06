/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.proxy;

import net.aoba.AobaClient;
import net.aoba.managers.proxymanager.Socks5Proxy;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class EditProxyScreen extends Screen {

	private final ProxyScreen parent;
	private final Socks5Proxy proxy;

	private ButtonWidget buttonSaveProxy;
	private TextFieldWidget textFieldProxyIp;
	private TextFieldWidget textFieldProxyPort;
	private TextFieldWidget textFieldProxyUsername;
	private TextFieldWidget textFieldProxyPassword;

	public EditProxyScreen(ProxyScreen parentScreen, Socks5Proxy proxy) {
		super(Text.of("Proxy Manager"));
		parent = parentScreen;
		this.proxy = proxy;
	}

	@Override
	protected void init() {
		super.init();

		textFieldProxyIp = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 76, 200, 20,
				Text.of("Enter IP"));
		textFieldProxyIp.setText(proxy == null ? "" : proxy.getIp());
		addDrawableChild(textFieldProxyIp);

		textFieldProxyPort = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 36, 200, 20,
				Text.of("Enter Port"));
		textFieldProxyPort.setText(proxy == null ? "" : String.valueOf(proxy.getPort()));
		addDrawableChild(textFieldProxyPort);

		textFieldProxyUsername = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 + 4, 200, 20,
				Text.of("Enter Username"));
		textFieldProxyUsername.setText(proxy == null ? "" : proxy.getUsername());
		addDrawableChild(textFieldProxyUsername);

		textFieldProxyPassword = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 + 44, 200, 20,
				Text.of("Enter Password"));
		textFieldProxyPassword.setText(proxy == null ? "" : proxy.getPassword());
		textFieldProxyPassword.setRenderTextProvider((text, n) -> {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < text.length(); i++)
				str.append("*");
			return OrderedText.styledForwardsVisitedString(str.toString(), Style.EMPTY);
		});
		addDrawableChild(textFieldProxyPassword);

		buttonSaveProxy = ButtonWidget.builder(Text.of("Save Proxy"), b -> onButtonProxyEditPressed())
				.dimensions(width / 2 - 100, height / 2 + 84, 200, 20).build();
		addDrawableChild(buttonSaveProxy);
		addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> onButtonCancelPressed())
				.dimensions(width / 2 - 100, height / 2 + 106, 200, 20).build());
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
		super.render(drawContext, mouseX, mouseY, partialTicks);
		drawContext.drawCenteredTextWithShadow(textRenderer, "Edit Proxy", width / 2, 20, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "IP:", width / 2 - 100, height / 2 - 90, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Port:", width / 2 - 100, height / 2 - 50, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Username:", width / 2 - 100, height / 2 - 10, 16777215);
		drawContext.drawTextWithShadow(textRenderer, "Password:", width / 2 - 100, height / 2 + 30, 16777215);
	}

	private void onButtonProxyEditPressed() {

		String ip = textFieldProxyIp.getText();
		String portText = textFieldProxyPort.getText();
		String username = textFieldProxyUsername.getText();
		String password = textFieldProxyPassword.getText();

		if (ip.isEmpty() || portText.isEmpty() || username.isEmpty() || password.isEmpty())
			return;

		try {
			proxy.setIp(ip);
			proxy.setPort(Integer.parseInt(portText));
			proxy.setUsername(username);
			proxy.setPassword(password);
			parent.refreshProxyList();
		} catch (NumberFormatException e) {
			AobaClient.LOGGER.error(e.getMessage());
		}

	}

	private void onButtonCancelPressed() {
		client.setScreen(parent);
	}
}
