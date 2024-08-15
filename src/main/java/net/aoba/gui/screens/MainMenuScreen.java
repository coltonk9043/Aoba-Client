package net.aoba.gui.screens;

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
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import static net.aoba.AobaClient.MC;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

public class MainMenuScreen extends Screen {
	protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
	protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(
			AOBA_PANORAMA_RENDERER);

	final int LOGO_HEIGHT = 70;
	final int BUTTON_WIDTH = 185;
	final int BUTTON_HEIGHT = 30;
	final int SPACING = 5;

	int smallScreenHeightOffset = 0;

	public MainMenuScreen() {
		super(Text.of("Aoba Client Main Menu"));
	}

	public void init() {
		super.init();

		if (this.height <= 650)
			smallScreenHeightOffset = 40;
		else
			smallScreenHeightOffset = 0;

		float widgetHeight = ((BUTTON_HEIGHT + SPACING) * 5);
		int startX = (int) ((this.width - this.BUTTON_WIDTH) / 2.0f);
		int startY = (int) ((this.height - widgetHeight) / 2) + smallScreenHeightOffset;

		// TODO: Left Alignment uses X coordinate of 50. Use this once news is done!
		AobaButtonWidget singleplayerButton = new AobaButtonWidget(startX, startY, BUTTON_WIDTH, BUTTON_HEIGHT,
				Text.of("Singleplayer"));
		singleplayerButton.setPressAction(b -> client.setScreen(new SelectWorldScreen(this)));
		this.addDrawableChild(singleplayerButton);

		AobaButtonWidget multiplayerButton = new AobaButtonWidget(startX, startY + BUTTON_HEIGHT + SPACING, BUTTON_WIDTH,
				BUTTON_HEIGHT, Text.of("Multiplayer"));
		multiplayerButton.setPressAction(b -> client.setScreen(new MultiplayerScreen(this)));
		this.addDrawableChild(multiplayerButton);

		AobaButtonWidget settingsButton = new AobaButtonWidget(startX, startY + ((BUTTON_HEIGHT + SPACING) * 2),
				BUTTON_WIDTH, BUTTON_HEIGHT, Text.of("Settings"));
		settingsButton.setPressAction(b -> client.setScreen(new OptionsScreen(this, MC.options)));
		this.addDrawableChild(settingsButton);

		AobaButtonWidget addonsButton = new AobaButtonWidget(startX, startY + ((BUTTON_HEIGHT + SPACING) * 3), BUTTON_WIDTH,
				BUTTON_HEIGHT, Text.of("Addons"));
		addonsButton.setPressAction(b -> client.setScreen(new AddonScreen(this)));
		this.addDrawableChild(addonsButton);

		AobaButtonWidget quitButton = new AobaButtonWidget(startX, startY + ((BUTTON_HEIGHT + SPACING) * 4), BUTTON_WIDTH,
				BUTTON_HEIGHT, Text.of("Quit"));
		quitButton.setPressAction(b -> client.stop());
		this.addDrawableChild(quitButton);

		AobaImageButtonWidget creditsButton = new AobaImageButtonWidget(this.width - 20 - 10, this.height - 20 - 10, 20,
				20, TextureBank.aoba);
		creditsButton.setPressAction(b -> MC.setScreen(new AobaCreditsScreen()));
		this.addDrawableChild(creditsButton);
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		super.render(drawContext, mouseX, mouseY, delta);

		 RenderSystem.disableCull();
         RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
         
		float widgetHeight = ((BUTTON_HEIGHT + SPACING) * 5);
		int startX = (int) ((this.width - BUTTON_WIDTH) / 2.0f);
		int startY = (int) ((this.height - widgetHeight) / 2) - LOGO_HEIGHT - 10 + smallScreenHeightOffset;

		drawContext.drawTexture(TextureBank.mainmenu_logo, startX, startY, 0, 0, BUTTON_WIDTH, LOGO_HEIGHT, 185,
				LOGO_HEIGHT);
		drawContext.drawTextWithShadow(this.textRenderer, "Aoba " + AobaClient.AOBA_VERSION, 2, this.height - 10,
				0xFF00FF);

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

				drawContext.drawTextWithShadow(this.textRenderer, addonName,
						this.width - addonNameWidth - byTextWidth - authorWidth - 2, yOffset, 0x50C878);

				drawContext.drawTextWithShadow(this.textRenderer, byText, this.width - byTextWidth - authorWidth - 2,
						yOffset, 0xFFFFFF);

				drawContext.drawTextWithShadow(this.textRenderer, author, this.width - authorWidth - 2, yOffset,
						0xFF0000);

				yOffset += 10;
			}
		}

		// News Peek!
		Render2D.drawOutlinedRoundedBox(drawContext.getMatrices().peek().getPositionMatrix(), width - 110, 30, 100,
				30, GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue(),
				GuiManager.backgroundColor.getValue());
		drawContext.drawTextWithShadow(this.textRenderer, "News coming soon!", width - 105, 40, Colors.WHITE);
		
		RenderSystem.enableCull();
	}

	@Override
	protected void renderPanoramaBackground(DrawContext context, float delta) {
		AOBA_ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, 1.0f, delta);
	}
}