package net.aoba.gui.screens;

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.aoba.gui.colors.Color;
import net.aoba.misc.Render2D;
import net.aoba.proxymanager.Socks5Proxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxySelectionList extends ElementListWidget<ProxySelectionList.Entry> {
    private final ProxyScreen owner;
    private final List<ProxySelectionList.NormalEntry> proxyList = new ArrayList();

    public ProxySelectionList(ProxyScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
        this.owner = ownerIn;
    }

    public abstract static class Entry extends ElementListWidget.Entry<ProxySelectionList.Entry> {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        ProxySelectionList.Entry ProxySelectionList$entry = this.getSelectedOrNull();
        return ProxySelectionList$entry != null && ProxySelectionList$entry.keyPressed(keyCode, scanCode, modifiers)
                || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void updateProxies() {
        this.clearEntries();

        for (Socks5Proxy proxy : this.owner.getProxyList()) {
            ProxySelectionList.NormalEntry entry = new ProxySelectionList.NormalEntry(this.owner, proxy);
            this.addEntry(entry);
        }
    }

    private void setList() {
        this.proxyList.forEach(this::addEntry);
    }

    public class NormalEntry extends ProxySelectionList.Entry {
        private final ProxyScreen owner;
        private final MinecraftClient mc;
        private final Socks5Proxy proxy;
        private long lastClickTime;

        protected NormalEntry(ProxyScreen ownerIn, Socks5Proxy proxy) {
            this.owner = ownerIn;
            this.proxy = proxy;
            this.mc = MinecraftClient.getInstance();
        }

        public void getProxyList() {
            this.owner.getProxyList();
        }

        public Socks5Proxy getProxyData() {
            return this.proxy;
        }

        @Override
        public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = this.mc.textRenderer;

            int lineHeight = 12; // Adjust this based on your font size and line spacing
            int textY = y + (entryHeight - lineHeight * 4) / 2; // Centering vertically
            
            drawContext.drawTextWithShadow(textRenderer, "IP: " + this.proxy.getIp(), x + 32 + 3, textY, 16777215);
            drawContext.drawTextWithShadow(textRenderer, "Port: " + this.proxy.getPort(), x + 32 + 3, textY + lineHeight, 16777215);
            drawContext.drawTextWithShadow(textRenderer, "Username: " + this.proxy.getUsername(), x + 32 + 3, textY + lineHeight * 2, 16777215);
            drawContext.drawText(textRenderer, "*".repeat(this.proxy.getPassword().length()), x + 32 + 3, textY + lineHeight * 3, 0x00FF00, true);
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return Collections.emptyList();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double d0 = mouseX - (double) ProxySelectionList.this.getRowLeft();

            if (d0 <= 32.0D) {
                if (d0 < 32.0D && d0 > 16.0D) {
                    this.owner.setSelected(this);
                    return true;
                }
            }
            this.owner.setSelected(this);
            if (Util.getMeasuringTimeMs() - this.lastClickTime < 250L) {
                this.owner.setActive();
            }
            this.lastClickTime = Util.getMeasuringTimeMs();
            return false;
        }
    }
}
