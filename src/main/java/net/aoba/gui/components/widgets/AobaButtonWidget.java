/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components.widgets;

import static net.aoba.AobaClient.MC;

import java.util.function.Consumer;

import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class AobaButtonWidget extends PressableWidget {
	private Consumer<AobaButtonWidget> pressAction;
	private long hoverStartTime = 0;
	private static final long HOVER_ANIMATION_DURATION = 200000000;

	public AobaButtonWidget(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

	public void setPressAction(Consumer<AobaButtonWidget> pressAction) {
		this.pressAction = pressAction;
	}

	@Override
	public void onPress() {
		if (pressAction != null) {
			pressAction.accept(this);
		}
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (isHovered() && hoverStartTime == 0) {
			hoverStartTime = System.nanoTime();
		} else if (!isHovered()) {
			hoverStartTime = 0;
		}

		long currentTime = System.nanoTime();
		float hoverProgress = hoverStartTime > 0 ? (currentTime - hoverStartTime) / (float) HOVER_ANIMATION_DURATION
				: 0;
		hoverProgress = Math.min(hoverProgress, 1.0f);
		hoverProgress = (float) Math.sin(hoverProgress * Math.PI / 2);

		Color boxColor = Color.interpolate(GuiManager.backgroundColor.getValue(), Color.convertHextoRGB("FFC0C0C0"),
				hoverProgress);
		Color outlineColor = Color.interpolate(GuiManager.borderColor.getValue(), Color.convertHextoRGB("C0C0C0"),
				hoverProgress);

		// RenderSystem.disableCull();

		// RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Render2D.drawOutlinedRoundedBox(context, getX(), getY(), width, height, GuiManager.roundingRadius.getValue(),
				outlineColor, boxColor);
		// RenderSystem.enableCull();

		int textWidth = MC.textRenderer.getWidth(getMessage().getString());
		int textHeight = MC.textRenderer.fontHeight;
		int textX = getX() + (width - textWidth) / 2;
		int textY = getY() + (height - textHeight) / 2 - (int) (2 * hoverProgress) + 2;

		Render2D.drawStringWithScale(context, getMessage().getString(), textX, textY,
				GuiManager.foregroundColor.getValue(), 1f);
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		// For brevity, we'll just skip this for now - if you want to add narration to
		// your widget, you can do so here.
	}
}
