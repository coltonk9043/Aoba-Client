/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens;

import static net.aoba.AobaClient.MC;

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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.RealmsMainScreen;
import net.aoba.AobaClient;
import net.aoba.api.IAddon;
import net.aoba.gui.components.widgets.AobaButtonWidget;
import net.aoba.gui.components.widgets.AobaImageButtonWidget;
import net.aoba.gui.screens.addons.AddonScreen;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class MainMenuScreen extends Screen {
	protected static final CubeMap AOBA_PANORAMA_RENDERER = new CubeMap(TextureBank.mainmenu_panorama);
	protected static final PanoramaRenderer AOBA_ROTATING_PANORAMA_RENDERER = new PanoramaRenderer(
			AOBA_PANORAMA_RENDERER);
	private static boolean panoramaRegistered = false;

	public static void registerPanoramaTextures(net.minecraft.client.renderer.texture.TextureManager textureManager) {
		if (!panoramaRegistered) {
			AOBA_PANORAMA_RENDERER.registerTextures(textureManager);
			panoramaRegistered = true;
		}
	}

	final int LOGO_HEIGHT = Math.max(58, height / 12);
	final int BUTTON_WIDTH = Math.max(150, width / 6);
	final int BUTTON_HEIGHT = Math.max(25, height / 20);
	final int SPACING = Math.max(5, height / 100);

	int smallScreenHeightOffset = 0;

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	private String fetchedVersion = null;

	public MainMenuScreen() {
		super(Component.nullToEmpty("Aoba Client Main Menu"));

		// Async fetch latest release from GitHub
		executor.execute(new Runnable() {
			@Override
			public void run() {
				fetchLatestVersion();
			}
		});
	}

	private static URI createURI(String url) {
		try {
			URI uri = new URI(url);
			return uri;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private void fetchLatestVersion() {
		try {
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
			if (status != HttpURLConnection.HTTP_OK) {
				throw new IllegalArgumentException("Device token could not be fetched. Invalid status code " + status);
			}

			JsonObject json = new Gson().fromJson(responseString, JsonObject.class);
			String tagName = json.get("tag_name").getAsString();
			if (tagName != null)
				fetchedVersion = tagName;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		super.init();

		if (height <= 650)
			smallScreenHeightOffset = 40;
		else
			smallScreenHeightOffset = 0;

		int columns = 2;
		int rows = 3;
		float widgetHeight = ((BUTTON_HEIGHT + SPACING) * rows);
		int startX = (int) ((width - (BUTTON_WIDTH * columns + SPACING * (columns - 1))) / 2.0f);
		int startY = (int) ((height - widgetHeight) / 2) + smallScreenHeightOffset;

		AobaButtonWidget singleplayerButton = new AobaButtonWidget(startX, startY, BUTTON_WIDTH, BUTTON_HEIGHT,
				Component.nullToEmpty("Singleplayer"));
		singleplayerButton.setPressAction(b -> minecraft.setScreen(new SelectWorldScreen(this)));
		addRenderableWidget(singleplayerButton);

		AobaButtonWidget multiplayerButton = new AobaButtonWidget(startX + BUTTON_WIDTH + SPACING, startY, BUTTON_WIDTH,
				BUTTON_HEIGHT, Component.nullToEmpty("Multiplayer"));
		multiplayerButton.setPressAction(b -> minecraft.setScreen(new JoinMultiplayerScreen(this)));
		addRenderableWidget(multiplayerButton);

		AobaButtonWidget realmsButton = new AobaButtonWidget(startX, startY + BUTTON_HEIGHT + SPACING, BUTTON_WIDTH,
				BUTTON_HEIGHT, Component.nullToEmpty("Realms"));
		realmsButton.setPressAction(b -> minecraft.setScreen(new RealmsMainScreen(this)));
		addRenderableWidget(realmsButton);

		AobaButtonWidget settingsButton = new AobaButtonWidget(startX + BUTTON_WIDTH + SPACING,
				startY + BUTTON_HEIGHT + SPACING, BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Settings"));
		settingsButton.setPressAction(b -> minecraft.setScreen(new OptionsScreen(this, MC.options)));
		addRenderableWidget(settingsButton);

		AobaButtonWidget addonsButton = new AobaButtonWidget(startX, startY + ((BUTTON_HEIGHT + SPACING) * 2),
				BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Addons"));
		addonsButton.setPressAction(b -> minecraft.setScreen(new AddonScreen(this)));
		addRenderableWidget(addonsButton);

		AobaButtonWidget quitButton = new AobaButtonWidget(startX + BUTTON_WIDTH + SPACING,
				startY + ((BUTTON_HEIGHT + SPACING) * 2), BUTTON_WIDTH, BUTTON_HEIGHT, Component.nullToEmpty("Quit"));
		quitButton.setPressAction(b -> minecraft.destroy());
		addRenderableWidget(quitButton);

		AobaImageButtonWidget creditsButton = new AobaImageButtonWidget(width - 20 - 10, height - 20 - 10, 20, 20,
				TextureBank.aoba);
		creditsButton.setPressAction(b -> MC.setScreen(new AobaCreditsScreen()));
		addRenderableWidget(creditsButton);

		AobaImageButtonWidget discordButton = new AobaImageButtonWidget(width - 60, height - 30, 20, 20,
				TextureBank.discord);
		discordButton.setPressAction(b -> Util.getPlatform().openUri("https://discord.gg/CDa4etPFtk"));
		addRenderableWidget(discordButton);

	}

	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
		super.render(drawContext, mouseX, mouseY, delta);

		float widgetHeight = (BUTTON_HEIGHT + SPACING) * 5;
		int startX = (width - BUTTON_WIDTH) / 2;
		int startY = (int) ((height - widgetHeight) / 2 + smallScreenHeightOffset);

		int logoWidth = (int) (LOGO_HEIGHT * (719.0 / 270.0));
		int logoX = (width - logoWidth) / 2;
		int logoY = startY - LOGO_HEIGHT - 10;
		drawContext.blit(RenderPipelines.GUI_TEXTURED, TextureBank.mainmenu_logo, logoX, logoY, 0, 0, logoWidth,
				LOGO_HEIGHT, 719, 270, 719, 270);

		drawContext.drawString(font, "Aoba " + AobaClient.AOBA_VERSION, 2, height - 10, 0xFFFF00FF);

		// Draw out of date if out of date.
		// TODO: Add option to hide if on previous versions.
		if (fetchedVersion != null && !fetchedVersion.equals(AobaClient.AOBA_VERSION)) {
			drawContext.drawString(font, "New version available: " + fetchedVersion, 2, height - 20,
					0xFFFF00FF);
		}

		if (AobaClient.addons.isEmpty()) {
			String noAddonsText = "No addons loaded";
			int textWidth = font.width(noAddonsText);
			drawContext.drawString(font, noAddonsText, width - textWidth - 15, 10, 0xFFFFFFFF);
		} else {
			int yOffset = 10;
			for (IAddon addon : AobaClient.addons) {
				String addonName = addon.getName();
				String byText = " by ";
				String author = addon.getAuthor();

				int addonNameWidth = font.width(addonName);
				int byTextWidth = font.width(byText);
				int authorWidth = font.width(author);

				drawContext.drawString(font, addonName,
						width - addonNameWidth - byTextWidth - authorWidth - 20, yOffset, 0xFF50C878);

				drawContext.drawString(font, byText, width - byTextWidth - authorWidth - 15, yOffset,
						0xFFFFFFFF);

				drawContext.drawString(font, author, width - authorWidth - 10, yOffset, 0xFFFF0000);

				yOffset += 10;
			}
		}

		/**
		 * int newsTextHeight = textRenderer.fontHeight; int newsBoxHeight =
		 * newsTextHeight + 20; int newsTextWidth = textRenderer.getWidth("Aoba " +
		 * fetchedVersion + " released!") + 10;
		 * Render2D.drawOutlinedRoundedBox(drawContext, width - newsTextWidth - 10, 30,
		 * newsTextWidth, newsBoxHeight, GuiManager.roundingRadius.getValue(),
		 * GuiManager.borderColor.getValue(), GuiManager.backgroundColor.getValue());
		 * drawContext.drawTextWithShadow(textRenderer, "Aoba " + fetchedVersion + "
		 * released!", width - newsTextWidth - 5, 40, Colors.WHITE);
		 **/
	}

	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
		renderPanorama(context, delta);
	}

	@Override
	protected void renderPanorama(GuiGraphics context, float delta) {
		if (!panoramaRegistered)
			return;

		try {
			AOBA_ROTATING_PANORAMA_RENDERER.render(context, width, height, true);
		} catch (IllegalStateException e) {
		}
	}
}