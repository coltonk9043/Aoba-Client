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
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class EditProxyScreen extends Screen {

	private final ProxyScreen parent;
	private final Socks5Proxy proxy;

	private Button buttonSaveProxy;
	private EditBox textFieldProxyIp;
	private EditBox textFieldProxyPort;
	private EditBox textFieldProxyUsername;
	private EditBox textFieldProxyPassword;

	public EditProxyScreen(ProxyScreen parentScreen, Socks5Proxy proxy) {
		super(Component.nullToEmpty("Proxy Manager"));
		parent = parentScreen;
		this.proxy = proxy;
	}

	@Override
	protected void init() {
		super.init();

		textFieldProxyIp = new EditBox(font, width / 2 - 100, height / 2 - 76, 200, 20,
				Component.nullToEmpty("Enter IP"));
		textFieldProxyIp.setValue(proxy == null ? "" : proxy.getIp());
		textFieldProxyIp.setResponder(s -> updateSaveButtonState());
		addRenderableWidget(textFieldProxyIp);

		textFieldProxyPort = new EditBox(font, width / 2 - 100, height / 2 - 36, 200, 20,
				Component.nullToEmpty("Enter Port"));
		textFieldProxyPort.setValue(proxy == null ? "" : String.valueOf(proxy.getPort()));
		textFieldProxyPort.setResponder(s -> updateSaveButtonState());
		addRenderableWidget(textFieldProxyPort);

		textFieldProxyUsername = new EditBox(font, width / 2 - 100, height / 2 + 4, 200, 20,
				Component.nullToEmpty("Enter Username"));
		textFieldProxyUsername.setValue(proxy == null || !proxy.hasUsername() ? "" : proxy.getUsername());
		addRenderableWidget(textFieldProxyUsername);

		textFieldProxyPassword = new EditBox(font, width / 2 - 100, height / 2 + 44, 200, 20,
				Component.nullToEmpty("Enter Password"));
		textFieldProxyPassword.setValue(proxy == null || !proxy.hasPassword() ? "" : proxy.getPassword());
		textFieldProxyPassword.addFormatter((text, offset) -> {
			return FormattedCharSequence.forward("*".repeat(text.length()), Style.EMPTY);
		});
		addRenderableWidget(textFieldProxyPassword);

		buttonSaveProxy = Button.builder(Component.nullToEmpty("Save Proxy"), b -> onButtonProxyEditPressed())
				.bounds(width / 2 - 100, height / 2 + 84, 200, 20).build();
		addRenderableWidget(buttonSaveProxy);
		addRenderableWidget(Button.builder(Component.nullToEmpty("Cancel"), b -> onButtonCancelPressed())
				.bounds(width / 2 - 100, height / 2 + 106, 200, 20).build());
	}

    @Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.centeredText(font, "Edit Proxy", width / 2, 20, 0xFFFFFFFF);
		graphics.text(font, "IP:", width / 2 - 100, height / 2 - 90, 0xFFFFFFFF);
		graphics.text(font, "Port:", width / 2 - 100, height / 2 - 50, 0xFFFFFFFF);
		graphics.text(font, "Username:", width / 2 - 100, height / 2 - 10, 0xFFFFFFFF);
		graphics.text(font, "Password:", width / 2 - 100, height / 2 + 30, 0xFFFFFFFF);
	}

	private void updateSaveButtonState() {
		buttonSaveProxy.active = !textFieldProxyIp.getValue().isEmpty() && !textFieldProxyPort.getValue().isEmpty();
	}

	private void onButtonProxyEditPressed() {
		String ip = textFieldProxyIp.getValue();
		String portText = textFieldProxyPort.getValue();
		String username = textFieldProxyUsername.getValue();
		String password = textFieldProxyPassword.getValue();

		try {
			proxy.setIp(ip);
			proxy.setPort(Integer.parseInt(portText));
			proxy.setUsername(username.isEmpty() ? null : username);
			proxy.setPassword(password.isEmpty() ? null : password);
			Aoba.getInstance().proxyManager.saveProxies();
			minecraft.setScreen(parent);
		} catch (NumberFormatException e) {
			AobaClient.LOGGER.error(e.getMessage());
		}
	}

	private void onButtonCancelPressed() {
		minecraft.setScreen(parent);
	}
}
