package net.aoba.gui.screens;

import net.aoba.AobaClient;
import net.aoba.api.IAddon;
import net.aoba.gui.components.widgets.AobaButtonWidget;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import static net.aoba.AobaClient.MC;

public class MainMenuScreen extends Screen {
    public MainMenuScreen() {
        super(Text.of("Aoba Client Main Menu"));
    }

    public void init() {
        super.init();

        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 10;

        int gridColumns = 2;
        int gridRows = 2;

        int totalGridWidth = gridColumns * buttonWidth + (gridColumns - 1) * spacing;
        int totalGridHeight = gridRows * buttonHeight + (gridRows - 1) * spacing;

        int startX = (this.width - totalGridWidth) / 2;
        int startY = (this.height - totalGridHeight) / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.of("Multiplayer"), b -> client.setScreen(new MultiplayerScreen(this)))
            .dimensions(startX, startY, buttonWidth, buttonHeight).build());
        this.addDrawableChild(ButtonWidget.builder(Text.of("Singleplayer"), b -> client.setScreen(new SelectWorldScreen(this)))
            .dimensions(startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight).build());
        this.addDrawableChild(ButtonWidget.builder(Text.of("Settings"), b -> client.setScreen(new OptionsScreen(this, MC.options)))
            .dimensions(startX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build());
        this.addDrawableChild(ButtonWidget.builder(Text.of("Quit"), b -> client.stop())
            .dimensions(startX + buttonWidth + spacing, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build());
//        Not implented yet.
//        this.addDrawableChild(ButtonWidget.builder(Text.of("ClickGui"), b -> client.setScreen(new ClickGuiScreen(this)))
//            .dimensions(xPosition, startY + 4 * (buttonHeight + spacing), buttonWidth, buttonHeight).build());

        int creditsButtonWidth = 20;
        int creditsButtonHeight = 20;
        int creditsButtonX = this.width - creditsButtonWidth - 10; // 10 pixels from the right edge
        int creditsButtonY = this.height - creditsButtonHeight - 10; // 10 pixels from the bottom edge
        this.addDrawableChild(ButtonWidget.builder(Text.of(""), b -> {
                // Add action for Credits button here
            })
            .dimensions(creditsButtonX, creditsButtonY, creditsButtonWidth, creditsButtonHeight).build());
        
        AobaButtonWidget aobaButtonWidget = new AobaButtonWidget(40, 80, 120, 20);
        
        this.addDrawableChild(aobaButtonWidget);
    }


    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawContext.drawTexture(TextureBank.mainmenu_logo, (this.width - 154) / 2, 20, 0, 0, 154, 67, 154, 67);
        drawContext.drawTextWithShadow(this.textRenderer, "Aoba " + AobaClient.AOBA_VERSION, 2, this.height - 10, 0xFF00FF);

        
        int creditsButtonWidth = 20;
        int creditsButtonHeight = 20;
        int creditsButtonX = this.width - creditsButtonWidth - 10;
        int creditsButtonY = this.height - creditsButtonHeight - 10;
        drawContext.drawTexture(TextureBank.aoba, creditsButtonX, creditsButtonY, 0, 0, creditsButtonWidth, creditsButtonHeight, creditsButtonWidth, creditsButtonHeight);
        if (AobaClient.addons.isEmpty()) {
            String noAddonsText = "No addons loaded";
            int textWidth = this.textRenderer.getWidth(noAddonsText);
            drawContext.drawTextWithShadow(this.textRenderer, noAddonsText, this.width - textWidth - 2, 10, 0xFFFFFF);
        } else {
            int yOffset = 10;
            for (IAddon addon : AobaClient.addons) {
                String addonName = addon.getName();
                String byText = " by ";
                String author = addon.getAuthor();

                int addonNameWidth = this.textRenderer.getWidth(addonName);
                int byTextWidth = this.textRenderer.getWidth(byText);
                int authorWidth = this.textRenderer.getWidth(author);

                drawContext.drawTextWithShadow(this.textRenderer, addonName, this.width - addonNameWidth - byTextWidth - authorWidth - 2, yOffset, 0x50C878);

                drawContext.drawTextWithShadow(this.textRenderer, byText, this.width - byTextWidth - authorWidth - 2, yOffset, 0xFFFFFF);

                drawContext.drawTextWithShadow(this.textRenderer, author, this.width - authorWidth - 2, yOffset, 0xFF0000);

                yOffset += 10;
            }
        }
    }
}