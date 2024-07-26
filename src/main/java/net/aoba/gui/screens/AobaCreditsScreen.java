package net.aoba.gui.screens;

import net.aoba.gui.GuiManager;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class AobaCreditsScreen extends Screen {
    protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
    protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(AOBA_PANORAMA_RENDERER);

    private static final List<String> CONTRIBUTORS = Arrays.asList(
        "coltonk9043",
        "cvs0",
        "Sukikoo",
        "Xateser",
        "Huckle"
    );

    private int currentContributorIndex = 0;
    private float animationProgress = 0.0f;
    private static final long ANIMATION_DURATION = 200000;

    public AobaCreditsScreen() {
        super(Text.of("Aoba Credits"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    animationProgress += delta / (ANIMATION_DURATION / 1000.0f);
    if (animationProgress >= 1.0f) {
        animationProgress = 0.0f;
        currentContributorIndex = (currentContributorIndex + 1) % CONTRIBUTORS.size();
    }

    int textHeight = this.textRenderer.fontHeight;
    int totalHeight = CONTRIBUTORS.size() * (textHeight + 10);
    int startY = this.height;
    int endY = -totalHeight;

    int baseY = (int) (startY + (endY - startY) * animationProgress);
    int logoHeight = 70;
    int logoY = baseY + 20; 
    
    context.drawTexture(TextureBank.mainmenu_logo, (this.width - 185) / 2, logoY - 100, 0, 0, 185, logoHeight, 185, logoHeight);


    int textWidth = this.textRenderer.getWidth(CONTRIBUTORS.get(0));
    int textX = (this.width - textWidth) / 2;

    for (int i = 0; i < CONTRIBUTORS.size(); i++) {
        int textY = baseY + i * (textHeight + 10);
        float alpha = getFadeAlpha(textY);
        drawContributorName(context, CONTRIBUTORS.get(i), textX, textY, alpha);
    }
}


    private float getFadeAlpha(int y) {
        float fadeHeight = this.height / 4.0f;
        float alpha = 1.0f;

        if (y < fadeHeight) {
            alpha = y / fadeHeight;
        } else if (y > this.height - fadeHeight) {
            alpha = (this.height - y) / fadeHeight;
        }

        return Math.max(0.0f, Math.min(1.0f, alpha));
    }

    private void drawContributorName(DrawContext context, String contributor, int x, int y, float alpha) {
        Render2D.drawString(context, contributor, (float) x, (float) y, GuiManager.foregroundColor.getValue());
    }
    
    @Override
    protected void renderPanoramaBackground(DrawContext context, float delta) {
        AOBA_ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, 1.0f, delta);
    }
}
