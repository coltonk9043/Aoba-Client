/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.proxy;

import net.aoba.Aoba;
import net.aoba.proxymanager.Socks5Proxy;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class EditProxyScreen extends Screen {

    private final ProxyScreen parent;
    private Socks5Proxy proxy;

    private ButtonWidget buttonSaveProxy;
    private TextFieldWidget textFieldProxyIp;
    private TextFieldWidget textFieldProxyPort;
    private TextFieldWidget textFieldProxyUsername;
    private TextFieldWidget textFieldProxyPassword;

    public EditProxyScreen(ProxyScreen parentScreen, Socks5Proxy proxy) {
        super(Text.of("Proxy Manager"));
        this.parent = parentScreen;
        this.proxy = proxy;
    }

    @Override
    protected void init() {
        super.init();

        this.textFieldProxyIp = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 76, 200, 20,
                Text.of("Enter IP"));
        this.textFieldProxyIp.setText(this.proxy == null ? "" : proxy.getIp());
        this.addDrawableChild(this.textFieldProxyIp);

        this.textFieldProxyPort = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 36, 200, 20,
                Text.of("Enter Port"));
        this.textFieldProxyPort.setText(this.proxy == null ? "" : String.valueOf(proxy.getPort()));
        this.addDrawableChild(this.textFieldProxyPort);

        this.textFieldProxyUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 + 4, 200, 20,
                Text.of("Enter Username"));
        this.textFieldProxyUsername.setText(this.proxy == null ? "" : proxy.getUsername());
        this.addDrawableChild(this.textFieldProxyUsername);

        this.textFieldProxyPassword = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 + 44, 200, 20,
                Text.of("Enter Password"));
        this.textFieldProxyPassword.setText(this.proxy == null ? "" : proxy.getPassword());
        this.textFieldProxyPassword.setRenderTextProvider((text, n) -> {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < text.length(); i++)
                str.append("*");
            return OrderedText.styledForwardsVisitedString(str.toString(), Style.EMPTY);
        });
        this.addDrawableChild(this.textFieldProxyPassword);

        this.buttonSaveProxy = ButtonWidget.builder(Text.of("Save Proxy"), b -> this.onButtonProxyEditPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 84, 200, 20).build();
        this.addDrawableChild(this.buttonSaveProxy);
        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> this.onButtonCancelPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 106, 200, 20).build());
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    	super.render(drawContext, mouseX, mouseY, partialTicks);
        drawContext.drawCenteredTextWithShadow(textRenderer, "Edit Proxy", this.width / 2, 20, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "IP:", this.width / 2 - 100, height / 2 - 90, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "Port:", this.width / 2 - 100, height / 2 - 50, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "Username:", this.width / 2 - 100, height / 2 - 10, 16777215);
        drawContext.drawTextWithShadow(textRenderer, "Password:", this.width / 2 - 100, height / 2 + 30, 16777215);
    }

    private void onButtonProxyEditPressed() {
        proxy.setIp(this.textFieldProxyIp.getText());
        proxy.setPort(Integer.parseInt(this.textFieldProxyPort.getText()));
        proxy.setUsername(this.textFieldProxyUsername.getText());
        proxy.setPassword(this.textFieldProxyPassword.getText());
        this.parent.refreshProxyList();
    }

    private void onButtonCancelPressed() {
        client.setScreen(this.parent);
    }
}
