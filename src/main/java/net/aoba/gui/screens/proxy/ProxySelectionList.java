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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Util;

public class ProxySelectionList extends ObjectSelectionList<ProxySelectionList.Entry> {
	private final ProxyScreen owner;

	public ProxySelectionList(ProxyScreen ownerIn, Minecraft minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
		owner = ownerIn;
	}

	public void setSelected(@org.jetbrains.annotations.Nullable ProxySelectionList.Entry entry) {
		super.setSelected(entry);
		if (entry != null) {
			owner.setEdittable();
		}
	}

	@Override
	public boolean keyPressed(net.minecraft.client.input.KeyEvent keyEvent) {
		Entry entry = getSelected();
		return entry != null && entry.keyPressed(keyEvent) || super.keyPressed(keyEvent);
	}

	public void updateProxies() {
		clearEntries();

		for (Socks5Proxy proxy : owner.getProxyList()) {
			ProxySelectionList.NormalEntry entry = new ProxySelectionList.NormalEntry(owner, proxy);
			addEntry(entry);
		}
	}

	@Environment(value = EnvType.CLIENT)
	public static abstract class Entry extends ObjectSelectionList.Entry<net.aoba.gui.screens.proxy.ProxySelectionList.Entry> implements AutoCloseable {
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
		public void renderContent(GuiGraphics drawContext, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int x = getX();
			int y = getY();
			int lineHeight = 12;

			int textColor = owner.isActiveProxy(proxy) ? 0xFF00FF00 : 0xFFFFFFFF;

			Render2D.drawStringWithScale(drawContext, "IP: " + proxy.getIp(), x + 32 + 3, y, textColor, 1.0f);
			Render2D.drawStringWithScale(drawContext, "Port: " + proxy.getPort(), x + 32 + 3, y + lineHeight,
					textColor, 1.0f);
			Render2D.drawStringWithScale(drawContext, "Username: " + proxy.getUsername(), x + 32 + 3,
					y + lineHeight * 2, textColor, 1.0f);
			Render2D.drawStringWithScale(drawContext, "*".repeat(proxy.getPassword().length()), x + 32 + 3,
					y + lineHeight * 3, textColor, 1.0f);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
			owner.setSelected(this);
			if (Util.getMillis() - lastClickTime < 250L) {
				Socks5Proxy proxy = getProxyData();
				if (owner.isActiveProxy(proxy)) {
					owner.resetActive();
				} else {
					owner.setActive();
				}
			}
			lastClickTime = Util.getMillis();
			return true;
		}

		@Override
		public Component getNarration() {
			return Component.nullToEmpty(proxy.getIp());
		}
	}
}
