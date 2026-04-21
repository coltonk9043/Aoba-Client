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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AddProxyScreen extends Screen {
	private final ProxyScreen parent;

	private Button buttonAddProxy;
	private EditBox textFieldProxyIp;
	private EditBox textFieldProxyPort;
	private EditBox textFieldProxyUsername;
	private EditBox textFieldProxyPassword;

	public AddProxyScreen(ProxyScreen parentScreen) {
		super(Component.nullToEmpty("Proxy Manager"));
		parent = parentScreen;
	}

	protected void init() {
		super.init();

		textFieldProxyIp = new EditBox(font, width / 2 - 100, height / 2 - 76, 200, 20,
				Component.nullToEmpty("Enter IP"));
		textFieldProxyIp.setValue("");
		textFieldProxyIp.setResponder(s -> updateAddButtonState());
		addRenderableWidget(textFieldProxyIp);

		textFieldProxyPort = new EditBox(font, width / 2 - 100, height / 2 - 36, 200, 20,
				Component.nullToEmpty("Enter Port"));
		textFieldProxyPort.setValue("");
		textFieldProxyPort.setResponder(s -> updateAddButtonState());
		addRenderableWidget(textFieldProxyPort);

		textFieldProxyUsername = new EditBox(font, width / 2 - 100, height / 2 + 4, 200, 20,
				Component.nullToEmpty("Enter Username"));
		textFieldProxyUsername.setValue("");
		addRenderableWidget(textFieldProxyUsername);

		textFieldProxyPassword = new EditBox(font, width / 2 - 100, height / 2 + 44, 200, 20,
				Component.nullToEmpty("Enter Password"));
		textFieldProxyPassword.setValue("");
		addRenderableWidget(textFieldProxyPassword);

		buttonAddProxy = Button.builder(Component.nullToEmpty("Add Proxy"), b -> onAddProxyButtonPressed())
				.bounds(width / 2 - 100, height / 2 + 74, 200, 20).build();
		buttonAddProxy.active = false;
		addRenderableWidget(buttonAddProxy);

		addRenderableWidget(Button.builder(Component.nullToEmpty("Cancel"), b -> onButtonCancelPressed())
				.bounds(width / 2 - 100, height / 2 + 98, 200, 20).build());
	}

	private void updateAddButtonState() {
		buttonAddProxy.active = !textFieldProxyIp.getValue().isEmpty() && !textFieldProxyPort.getValue().isEmpty();
	}

	private void onAddProxyButtonPressed() {
		String ip = textFieldProxyIp.getValue();
		String portText = textFieldProxyPort.getValue();
		String username = textFieldProxyUsername.getValue();
		String password = textFieldProxyPassword.getValue();

		try {
			int port = Integer.parseInt(portText);
			Socks5Proxy newProxy;
			if (username.isEmpty() && password.isEmpty()) {
				newProxy = new Socks5Proxy(ip, port);
			} else {
				newProxy = new Socks5Proxy(ip, port, username, password);
			}
			Aoba.getInstance().proxyManager.addProxy(newProxy);
			minecraft.setScreen(parent);
		} catch (NumberFormatException e) {
			AobaClient.LOGGER.error(e.getMessage());
		}
	}

	public void onButtonCancelPressed() {
		minecraft.setScreen(parent);
	}

    @Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.centeredText(font, "Add Proxy", width / 2, 20, 0xFFFFFFFF);

		graphics.text(font, "IP Address:", width / 2 - 100, height / 2 - 90, 0xFFFFFFFF);
		graphics.text(font, "Port:", width / 2 - 100, height / 2 - 50, 0xFFFFFFFF);
		graphics.text(font, "Username:", width / 2 - 100, height / 2 - 10, 0xFFFFFFFF);
		graphics.text(font, "Password:", width / 2 - 100, height / 2 + 30, 0xFFFFFFFF);
	}
}
