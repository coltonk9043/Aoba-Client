/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.aoba.AobaClient;
import net.aoba.api.IAddon;
import net.aoba.gui.GuiManager;
import net.aoba.gui.components.widgets.AobaButtonWidget;
import net.aoba.gui.components.widgets.AobaImageButtonWidget;
import net.aoba.gui.screens.addons.AddonScreen;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.aoba.AobaClient.MC;

public class MainMenuScreen extends Screen
{
    protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
    protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(
            AOBA_PANORAMA_RENDERER);

    final int LOGO_HEIGHT = Math.max(58, this.height / 12);
    final int BUTTON_WIDTH = Math.max(150, this.width / 6);
    final int BUTTON_HEIGHT = Math.max(25, this.height / 20);
    final int SPACING = Math.max(5, this.height / 100);

    int smallScreenHeightOffset = 0;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String fetchedVersion = null;

    public MainMenuScreen()
    {
        super(Text.of("Aoba Client Main Menu"));

        // Async fetch latest release from GitHub
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                fetchLatestVersion();
            }
        });
    }

    private static URI createURI(String url)
    {
        try
        {
            URI uri = new URI(url);
            return uri;
        } catch (URISyntaxException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    private void fetchLatestVersion()
    {
        try
        {
            HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).followRedirects(Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest
                    .newBuilder(
                            createURI("https://api.github.com/repos/coltonk9043/Aoba-MC-Hacked-Client/releases/latest"))
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36")
                    .header("Accept", "application/json").header("Content-Type", "application/x-www-form-urlencoded")
                    .GET().build();

            HttpResponse<String> response;
            response = client.send(request, BodyHandlers.ofString());
            String responseString = response.body();

            int status = response.statusCode();
            if (status != HttpURLConnection.HTTP_OK)
            {
                throw new IllegalArgumentException("Device token could not be fetched. Invalid status code " + status);
            }

            JsonObject json = new Gson().fromJson(responseString, JsonObject.class);
            String tagName = json.get("tag_name").getAsString();
            if (tagName != null)
                fetchedVersion = tagName;
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void init()
    {
        super.init();

        if (this.height <= 650)
            smallScreenHeightOffset = 40;
        else
            smallScreenHeightOffset = 0;

        int columns = 2;
        int rows = 3;
        float widgetHeight = ((BUTTON_HEIGHT + SPACING) * rows);
        int startX = (int) ((this.width - (BUTTON_WIDTH * columns + SPACING * (columns - 1))) / 2.0f);
        int startY = (int) ((this.height - widgetHeight) / 2) + smallScreenHeightOffset;

        AobaButtonWidget singleplayerButton = new AobaButtonWidget(startX, startY, BUTTON_WIDTH, BUTTON_HEIGHT,
                Text.of("Singleplayer"));
        singleplayerButton.setPressAction(b -> client.setScreen(new SelectWorldScreen(this)));
        this.addDrawableChild(singleplayerButton);

        AobaButtonWidget multiplayerButton = new AobaButtonWidget(startX + BUTTON_WIDTH + SPACING, startY,
                BUTTON_WIDTH, BUTTON_HEIGHT, Text.of("Multiplayer"));
        multiplayerButton.setPressAction(b -> client.setScreen(new MultiplayerScreen(this)));
        this.addDrawableChild(multiplayerButton);

        AobaButtonWidget realmsButton = new AobaButtonWidget(startX, startY + BUTTON_HEIGHT + SPACING,
                BUTTON_WIDTH, BUTTON_HEIGHT, Text.of("Realms"));
        realmsButton.setPressAction(b -> client.setScreen(new RealmsMainScreen(this)));
        this.addDrawableChild(realmsButton);

        AobaButtonWidget settingsButton = new AobaButtonWidget(startX + BUTTON_WIDTH + SPACING, startY + BUTTON_HEIGHT + SPACING,
                BUTTON_WIDTH, BUTTON_HEIGHT, Text.of("Settings"));
        settingsButton.setPressAction(b -> client.setScreen(new OptionsScreen(this, MC.options)));
        this.addDrawableChild(settingsButton);

        AobaButtonWidget addonsButton = new AobaButtonWidget(startX, startY + ((BUTTON_HEIGHT + SPACING) * 2),
                BUTTON_WIDTH, BUTTON_HEIGHT, Text.of("Addons"));
        addonsButton.setPressAction(b -> client.setScreen(new AddonScreen(this)));
        this.addDrawableChild(addonsButton);

        AobaButtonWidget quitButton = new AobaButtonWidget(startX + BUTTON_WIDTH + SPACING, startY + ((BUTTON_HEIGHT + SPACING) * 2),
                BUTTON_WIDTH, BUTTON_HEIGHT, Text.of("Quit"));
        quitButton.setPressAction(b -> client.stop());
        this.addDrawableChild(quitButton);

        AobaImageButtonWidget creditsButton = new AobaImageButtonWidget(this.width - 20 - 10, this.height - 20 - 10, 20,
                20, TextureBank.aoba);
        creditsButton.setPressAction(b -> MC.setScreen(new AobaCreditsScreen()));
        this.addDrawableChild(creditsButton);

        AobaImageButtonWidget discordButton = new AobaImageButtonWidget(this.width - 60, this.height - 30, 20, 20,
                TextureBank.discord);
        discordButton.setPressAction(b -> Util.getOperatingSystem().open("https://discord.gg/CDa4etPFtk"));
        this.addDrawableChild(discordButton);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta)
    {
        super.render(drawContext, mouseX, mouseY, delta);

        RenderSystem.disableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


        float widgetHeight = (BUTTON_HEIGHT + SPACING) * 5;
        int startX = (this.width - BUTTON_WIDTH) / 2;
        int startY = (int) ((this.height - widgetHeight) / 2 + smallScreenHeightOffset);

        int logoWidth = (int) (LOGO_HEIGHT * (185.0 / LOGO_HEIGHT));
        int logoX = (this.width - logoWidth) / 2;
        int logoY = startY - LOGO_HEIGHT - 10;
        drawContext.drawTexture(RenderLayer::getGuiTextured, TextureBank.mainmenu_logo, logoX, logoY, 0, 0, logoWidth, LOGO_HEIGHT, 185, LOGO_HEIGHT);


        drawContext.drawTextWithShadow(this.textRenderer, "Aoba " + AobaClient.AOBA_VERSION, 2, this.height - 10,
                0xFF00FF);

        // Draw out of date if out of date.
        // TODO: Add option to hide if on previous versions.
        if (fetchedVersion != null && !fetchedVersion.equals(AobaClient.AOBA_VERSION))
        {
            drawContext.drawTextWithShadow(this.textRenderer, "New version available: " + fetchedVersion, 2,
                    this.height - 20, 0xFF00FF);
        }

        if (AobaClient.addons.isEmpty())
        {
            String noAddonsText = "No addons loaded";
            int textWidth = this.textRenderer.getWidth(noAddonsText);
            drawContext.drawTextWithShadow(this.textRenderer, noAddonsText, this.width - textWidth - 15, 10, 0xFFFFFF);
        }
        else
        {
            int yOffset = 10;
            for (IAddon addon : AobaClient.addons)
            {
                String addonName = addon.getName();
                String byText = " by ";
                String author = addon.getAuthor();

                int addonNameWidth = this.textRenderer.getWidth(addonName);
                int byTextWidth = this.textRenderer.getWidth(byText);
                int authorWidth = this.textRenderer.getWidth(author);

                drawContext.drawTextWithShadow(this.textRenderer, addonName,
                        this.width - addonNameWidth - byTextWidth - authorWidth - 20, yOffset, 0x50C878);

                drawContext.drawTextWithShadow(this.textRenderer, byText,
                        this.width - byTextWidth - authorWidth - 15, yOffset, 0xFFFFFF);

                drawContext.drawTextWithShadow(this.textRenderer, author,
                        this.width - authorWidth - 10, yOffset, 0xFF0000);


                yOffset += 10;
            }
        }

        int newsTextHeight = this.textRenderer.fontHeight;
        int newsBoxHeight = newsTextHeight + 20;
        int newsTextWidth = this.textRenderer.getWidth("Aoba " + fetchedVersion + " released!") + 10;
        Render2D.drawOutlinedRoundedBox(drawContext.getMatrices().peek().getPositionMatrix(), width - newsTextWidth - 10, 30, newsTextWidth, newsBoxHeight,
                GuiManager.roundingRadius.getValue(),
                GuiManager.borderColor.getValue(),
                GuiManager.backgroundColor.getValue()
        );
        drawContext.drawTextWithShadow(this.textRenderer, "Aoba " + fetchedVersion + " released!", width - newsTextWidth - 5, 40, Colors.WHITE);


        RenderSystem.enableCull();
    }

    @Override
    protected void renderPanoramaBackground(DrawContext context, float delta)
    {
        AOBA_ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, 1.0f, delta);
    }
}