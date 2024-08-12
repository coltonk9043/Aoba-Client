package net.aoba.gui.screens.proxy;

import net.aoba.utils.render.Render2D;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.aoba.proxymanager.Socks5Proxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import java.util.ArrayList;
import java.util.List;

public class ProxySelectionList extends AlwaysSelectedEntryListWidget<ProxySelectionList.Entry> {
    private final ProxyScreen owner;
    private final List<ProxySelectionList.NormalEntry> proxyList = new ArrayList<>();

    public ProxySelectionList(ProxyScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
        this.owner = ownerIn;
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

    
    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> implements AutoCloseable {
        @Override
        public void close() {}
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

            int lineHeight = 12;
            int textY = y + (entryHeight - lineHeight * 4) / 2;

            int textColor = this.owner.isActiveProxy(this.proxy) ? 0x00FF00 : 16777215;

            Render2D.drawStringWithScale(drawContext, "IP: " + this.proxy.getIp(), x + 32 + 3, textY, textColor, 1.0f);
            Render2D.drawStringWithScale(drawContext, "Port: " + this.proxy.getPort(), x + 32 + 3, textY + lineHeight, textColor, 1.0f);
            Render2D.drawStringWithScale(drawContext, "Username: " + this.proxy.getUsername(), x + 32 + 3, textY + lineHeight * 2, textColor, 1.0f);
            Render2D.drawStringWithScale(drawContext, "*".repeat(this.proxy.getPassword().length()), x + 32 + 3, textY + lineHeight * 3, textColor, 1.0f);
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
                Socks5Proxy proxy = this.getProxyData();
                if (this.owner.isActiveProxy(proxy)) {
                    this.owner.resetActive();
                } else {
                    this.owner.setActive();
                }
            }
            this.lastClickTime = Util.getMeasuringTimeMs();
            return false;
        }

		@Override
		public Text getNarration() {
			return Text.of(proxy.getIp());
		}
    }
}
