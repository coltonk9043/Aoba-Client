package net.aoba.gui.screens.proxy;

import net.aoba.Aoba;
import net.aoba.proxymanager.Socks5Proxy;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;

public class ProxyScreen extends Screen {
	protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
	protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(AOBA_PANORAMA_RENDERER);
	
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

        this.proxyListSelector = new ProxySelectionList(this, this.client, this.width, this.height, 32, 64);
        this.proxyListSelector.updateProxies();
        this.proxyListSelector.setDimensionsAndPosition(this.width, this.height - 70, 0, 32);
        this.addDrawableChild(this.proxyListSelector);


        this.addDrawableChild(ButtonWidget.builder(Text.of("Add Proxy"), b -> client.setScreen(new AddProxyScreen(this)))
                .dimensions(this.width / 2 - 205, this.height - 28, 100, 20).build());

        this.editButton = ButtonWidget.builder(Text.of("Edit Alt"), b -> this.editSelected())
                .dimensions(this.width / 2 - 100 , this.height - 28, 100, 20).build();
        this.editButton.active = false;
        this.addDrawableChild(this.editButton);
        
        this.deleteButton = ButtonWidget.builder(Text.of("Delete Proxy"), b -> this.deleteSelected())
                .dimensions(this.width / 2 + 5, this.height - 28, 100, 20).build();
        this.deleteButton.active = false;
        this.addDrawableChild(this.deleteButton);
        
        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parentScreen))
                .dimensions(this.width / 2 + 110, this.height - 28, 100, 20).build());
    }

    public ArrayList<Socks5Proxy> getProxyList() {
        return Aoba.getInstance().proxyManager.getProxies();
    }

    public void refreshProxyList() {
        this.client.setScreen(new ProxyScreen(this.parentScreen));
    }

    public void setSelected(ProxySelectionList.Entry selected) {
        this.proxyListSelector.setSelected(selected);
        this.setEdittable();
    }

    public void editSelected() {
        Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) this.proxyListSelector.getSelectedOrNull()).getProxyData();
        if (proxy == null) {
            return;
        }
        client.setScreen(new EditProxyScreen(this, proxy));
    }

    public void deleteSelected() {
        Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) this.proxyListSelector.getSelectedOrNull()).getProxyData();
        if (proxy == null) {
            return;
        }
        Aoba.getInstance().proxyManager.removeProxy(proxy);
        this.refreshProxyList();
    }

    protected void setEdittable() {
        this.editButton.active = true;
        this.deleteButton.active = true;
    }

    public boolean isActiveProxy(Socks5Proxy proxy) {
        return Aoba.getInstance().proxyManager.getActiveProxy() == proxy;
    }

    public void setActive() {
        ProxySelectionList.Entry proxyListSelectorSelectedOrNull = this.proxyListSelector.getSelectedOrNull();

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
		AOBA_ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, 1.0f, delta);
	}
}
