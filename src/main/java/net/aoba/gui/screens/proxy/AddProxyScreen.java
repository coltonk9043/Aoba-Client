package net.aoba.gui.screens.proxy;

import net.aoba.Aoba;
import net.aoba.proxymanager.Socks5Proxy;
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
        this.parent = parentScreen;
    }

    protected void init() {
        super.init();

        this.textFieldProxyIp = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20, Text.of("Enter IP"));
        this.textFieldProxyIp.setText("");
        this.addDrawableChild(this.textFieldProxyIp);

        this.textFieldProxyPort = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 36, 200, 20, Text.of("Enter Port"));
        this.textFieldProxyPort.setText("");
        this.addDrawableChild(this.textFieldProxyPort);

        this.textFieldProxyUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 + 4, 200, 20, Text.of("Enter Username"));
        this.textFieldProxyUsername.setText("");
        this.addDrawableChild(this.textFieldProxyUsername);

        this.textFieldProxyPassword = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 + 44, 200, 20, Text.of("Enter Password"));
        this.textFieldProxyPassword.setText("");
        this.addDrawableChild(this.textFieldProxyPassword);

        this.buttonAddProxy = ButtonWidget.builder(Text.of("Add Proxy"), b -> this.onAddProxyButtonPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 94, 200, 20).build();
        this.addDrawableChild(this.buttonAddProxy);

        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> this.onButtonCancelPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 124, 200, 20).build());
    }

    private void onAddProxyButtonPressed() {
        String ip = this.textFieldProxyIp.getText();
        int port = Integer.parseInt(this.textFieldProxyPort.getText());
        String username = this.textFieldProxyUsername.getText();
        String password = this.textFieldProxyPassword.getText();

        Socks5Proxy newProxy = new Socks5Proxy(ip, port, username, password);
        Aoba.getInstance().proxyManager.addProxy(newProxy);
        this.parent.refreshProxyList();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    	super.render(drawContext, mouseX, mouseY, delta);
        drawContext.drawCenteredTextWithShadow(this.textRenderer, "Add Proxy", this.width / 2, 20, 16777215);
        
        drawContext.drawTextWithShadow(textRenderer, "IP Address:", this.width / 2 - 100, height / 2 - 90, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "Port:", this.width / 2 - 100, height / 2 - 50, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "Username:", this.width / 2 - 100, height / 2 - 10, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "Password:", this.width / 2 - 100, height / 2 + 30, 16777215);
    }

    public void onButtonCancelPressed() {
        client.setScreen(this.parent);
    }
}
