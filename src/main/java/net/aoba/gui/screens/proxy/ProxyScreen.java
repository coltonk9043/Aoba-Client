/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.proxy;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.managers.proxymanager.Socks5Proxy;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ProxyScreen extends Screen {
	protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
	protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(
			AOBA_PANORAMA_RENDERER);

	private final Screen parentScreen;
	private ProxySelectionList proxyListSelector;
	private ButtonWidget editButton;
	private ButtonWidget deleteButton;

	public ProxyScreen(Screen parentScreen) {
		super(Text.of("Alt Manager"));
		this.parentScreen = parentScreen;
	}

	public void init() {
		super.init();

		proxyListSelector = new ProxySelectionList(this, client, width, height, 32, 64);
		proxyListSelector.updateProxies();
		proxyListSelector.setDimensionsAndPosition(width, height - 70, 0, 32);
		addDrawableChild(proxyListSelector);

		addDrawableChild(ButtonWidget.builder(Text.of("Add Proxy"), b -> client.setScreen(new AddProxyScreen(this)))
				.dimensions(width / 2 - 205, height - 28, 100, 20).build());

		editButton = ButtonWidget.builder(Text.of("Edit Alt"), b -> editSelected())
				.dimensions(width / 2 - 100, height - 28, 100, 20).build();
		editButton.active = false;
		addDrawableChild(editButton);

		deleteButton = ButtonWidget.builder(Text.of("Delete Proxy"), b -> deleteSelected())
				.dimensions(width / 2 + 5, height - 28, 100, 20).build();
		deleteButton.active = false;
		addDrawableChild(deleteButton);

		addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(parentScreen))
				.dimensions(width / 2 + 110, height - 28, 100, 20).build());
	}

	public ArrayList<Socks5Proxy> getProxyList() {
		return Aoba.getInstance().proxyManager.getProxies();
	}

	public void refreshProxyList() {
		client.setScreen(new ProxyScreen(parentScreen));
	}

	public void setSelected(ProxySelectionList.Entry selected) {
		proxyListSelector.setSelected(selected);
		setEdittable();
	}

	public void editSelected() {
		Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) proxyListSelector.getSelectedOrNull()).getProxyData();
		if (proxy == null) {
			return;
		}
		client.setScreen(new EditProxyScreen(this, proxy));
	}

	public void deleteSelected() {
		Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) proxyListSelector.getSelectedOrNull()).getProxyData();
		if (proxy == null) {
			return;
		}
		Aoba.getInstance().proxyManager.removeProxy(proxy);
		refreshProxyList();
	}

	protected void setEdittable() {
		editButton.active = true;
		deleteButton.active = true;
	}

	public boolean isActiveProxy(Socks5Proxy proxy) {
		return Aoba.getInstance().proxyManager.getActiveProxy() == proxy;
	}

	public void setActive() {
		ProxySelectionList.Entry proxyListSelectorSelectedOrNull = proxyListSelector.getSelectedOrNull();

		if (proxyListSelectorSelectedOrNull == null) {
			return;
		}

		Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) proxyListSelectorSelectedOrNull).getProxyData();
		Aoba.getInstance().proxyManager.setActiveProxy(proxy);
	}

	public void resetActive() {
		Aoba.getInstance().proxyManager.setActiveProxy(null);
	}

	@Override
	protected void renderPanoramaBackground(DrawContext context, float delta) {
		// AOBA_ROTATING_PANORAMA_RENDERER.render(context, width, height, 1.0f, delta);
	}
}
