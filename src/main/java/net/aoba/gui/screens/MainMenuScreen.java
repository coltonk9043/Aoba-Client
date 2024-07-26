package net.aoba.gui.screens;

import net.aoba.AobaClient;
import net.aoba.api.IAddon;
import net.aoba.gui.components.widgets.AobaButtonWidget;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import static net.aoba.AobaClient.MC;

import org.lwjgl.opengl.GL11;

public class MainMenuScreen extends Screen {
	protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
	protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(AOBA_PANORAMA_RENDERER);
	
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

        AobaButtonWidget singleplayerButton = new AobaButtonWidget(startX , startY, buttonWidth, buttonHeight, Text.of("Singleplayer"));
        singleplayerButton.setPressAction(b -> client.setScreen(new SelectWorldScreen(this)));
        this.addDrawableChild(singleplayerButton);
        
        AobaButtonWidget multiplayerButton = new AobaButtonWidget(startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight, Text.of("Multiplayer"));
        multiplayerButton.setPressAction(b -> client.setScreen(new MultiplayerScreen(this)));
        this.addDrawableChild(multiplayerButton);

        AobaButtonWidget settingsButton = new AobaButtonWidget(startX, startY + buttonHeight + spacing, buttonWidth, buttonHeight, Text.of("Settings"));
        settingsButton.setPressAction(b -> client.setScreen(new OptionsScreen(this, MC.options)));
        this.addDrawableChild(settingsButton);

        AobaButtonWidget quitButton = new AobaButtonWidget(startX + buttonWidth + spacing, startY + buttonHeight + spacing, buttonWidth, buttonHeight, Text.of("Quit"));
        quitButton.setPressAction(b -> client.stop());
        this.addDrawableChild(quitButton);
    }


    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        
        drawContext.drawTexture(TextureBank.mainmenu_logo, (this.width - 185) / 2, 20, 0, 0, 185, 70, 185, 70);
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
    
    @Override
    protected void renderPanoramaBackground(DrawContext context, float delta) {
    	AOBA_ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, 1.0f, delta);
    }
}