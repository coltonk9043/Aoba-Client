package net.aoba.gui.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ProxyScreen extends Screen {
    private final Screen parentScreen;
    public ProxyScreen(Screen parentScreen) {
        super(Text.of("Alt Manager"));
        this.parentScreen = parentScreen;
    }

    public void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), button -> client.setScreen(parentScreen))
                .dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Coming Soon"), button -> {})
                .dimensions(this.width / 2 - 50, this.height / 2 - 10, 100, 20).build());
    }
}
