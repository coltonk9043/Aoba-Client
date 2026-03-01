/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens;

import java.util.Arrays;
import java.util.List;

import net.aoba.utils.render.Render2D;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class AobaCreditsScreen extends Screen {
	protected static final CubeMap AOBA_PANORAMA_RENDERER = new CubeMap(TextureBank.mainmenu_panorama);
	protected static final PanoramaRenderer AOBA_ROTATING_PANORAMA_RENDERER = new PanoramaRenderer(
			AOBA_PANORAMA_RENDERER);

	private static final List<String> CONTRIBUTORS = Arrays.asList("coltonk9043", "cvs0", "Tewxx", "OsakiTsukiko", "Logging4J", "TangyKiwi", "Xatsec", "BatchDebug");

	private int currentContributorIndex = 0;
	private float animationProgress = 0.0f;
	private static final long ANIMATION_DURATION = 200000;

	public AobaCreditsScreen() {
		super(Component.nullToEmpty("Aoba Credits"));
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		animationProgress += delta / (ANIMATION_DURATION / 1000.0f);
		if (animationProgress >= 1.0f) {
			animationProgress = 0.0f;
			currentContributorIndex = (currentContributorIndex + 1) % CONTRIBUTORS.size();
		}

		int textHeight = font.lineHeight;
		int totalHeight = CONTRIBUTORS.size() * (textHeight + 10);
		int startY = height;
		int endY = -totalHeight;

		int baseY = (int) (startY + (endY - startY) * animationProgress);
		int logoHeight = 70;
		int logoY = baseY + 20;

		context.blit(RenderPipelines.GUI_TEXTURED, TextureBank.mainmenu_logo, (width - 185) / 2, logoY - 100,
				0, 0, 185, logoHeight, 185, 70, 185, 70);

		for (int i = 0; i < CONTRIBUTORS.size(); i++) {
			int textWidth = font.width(CONTRIBUTORS.get(i));
			int textX = (width - (textWidth * 2)) / 2;
			int textY = baseY + i * (textHeight + 10);
			float alpha = getFadeAlpha(textY);
			drawContributorName(context, CONTRIBUTORS.get(i), textX, textY, alpha);
		}
	}

	private float getFadeAlpha(int y) {
		float fadeHeight = height / 4.0f;
		float alpha = 1.0f;

		if (y < fadeHeight) {
			alpha = y / fadeHeight;
		} else if (y > height - fadeHeight) {
			alpha = (height - y) / fadeHeight;
		}

		return Math.max(0.0f, Math.min(1.0f, alpha));
	}

	private void drawContributorName(GuiGraphics context, String contributor, int x, int y, float alpha) {
		Render2D.drawString(context, contributor, (float) x, (float) y, CommonColors.WHITE);
	}

	@Override
	protected void renderPanorama(GuiGraphics context, float delta) {
		AOBA_ROTATING_PANORAMA_RENDERER.render(context, width, height, true);
	}
}
