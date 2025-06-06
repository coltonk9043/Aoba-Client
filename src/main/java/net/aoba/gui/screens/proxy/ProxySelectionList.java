/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.proxy;

import net.aoba.managers.proxymanager.Socks5Proxy;
import net.aoba.utils.render.Render2D;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class ProxySelectionList extends AlwaysSelectedEntryListWidget<ProxySelectionList.Entry> {
	private final ProxyScreen owner;

	public ProxySelectionList(ProxyScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
		owner = ownerIn;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		ProxySelectionList.Entry ProxySelectionList$entry = getSelectedOrNull();
		return ProxySelectionList$entry != null && ProxySelectionList$entry.keyPressed(keyCode, scanCode, modifiers)
				|| super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void updateProxies() {
		clearEntries();

		for (Socks5Proxy proxy : owner.getProxyList()) {
			ProxySelectionList.NormalEntry entry = new ProxySelectionList.NormalEntry(owner, proxy);
			addEntry(entry);
		}
	}

	@Environment(value = EnvType.CLIENT)
	public static abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> implements AutoCloseable {
		@Override
		public void close() {
		}
	}

	public class NormalEntry extends ProxySelectionList.Entry {
		private final ProxyScreen owner;
		private final Socks5Proxy proxy;
		private long lastClickTime;

		protected NormalEntry(ProxyScreen ownerIn, Socks5Proxy proxy) {
			owner = ownerIn;
			this.proxy = proxy;
		}

		public void getProxyList() {
			owner.getProxyList();
		}

		public Socks5Proxy getProxyData() {
			return proxy;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int lineHeight = 12;
			int textY = y + (entryHeight - lineHeight * 4) / 2;

			int textColor = owner.isActiveProxy(proxy) ? 0x00FF00 : 16777215;

			Render2D.drawStringWithScale(drawContext, "IP: " + proxy.getIp(), x + 32 + 3, textY, textColor, 1.0f);
			Render2D.drawStringWithScale(drawContext, "Port: " + proxy.getPort(), x + 32 + 3, textY + lineHeight,
					textColor, 1.0f);
			Render2D.drawStringWithScale(drawContext, "Username: " + proxy.getUsername(), x + 32 + 3,
					textY + lineHeight * 2, textColor, 1.0f);
			Render2D.drawStringWithScale(drawContext, "*".repeat(proxy.getPassword().length()), x + 32 + 3,
					textY + lineHeight * 3, textColor, 1.0f);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d0 = mouseX - (double) getRowLeft();

			if (d0 <= 32.0D) {
				if (d0 < 32.0D && d0 > 16.0D) {
					owner.setSelected(this);
					return true;
				}
			}
			owner.setSelected(this);
			if (Util.getMeasuringTimeMs() - lastClickTime < 250L) {
				Socks5Proxy proxy = getProxyData();
				if (owner.isActiveProxy(proxy)) {
					owner.resetActive();
				} else {
					owner.setActive();
				}
			}
			lastClickTime = Util.getMeasuringTimeMs();
			return false;
		}

		@Override
		public Text getNarration() {
			return Text.of(proxy.getIp());
		}
	}
}
