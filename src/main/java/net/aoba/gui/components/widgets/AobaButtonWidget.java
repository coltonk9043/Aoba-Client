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
import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class AobaButtonWidget extends AbstractButton {
	private Consumer<AobaButtonWidget> pressAction;
	private long hoverStartTime = 0;
	private static final long HOVER_ANIMATION_DURATION = 200000000;

	public AobaButtonWidget(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	public void setPressAction(Consumer<AobaButtonWidget> pressAction) {
		this.pressAction = pressAction;
	}

	@Override
	public void onPress(InputWithModifiers input) {
		if (pressAction != null) {
			pressAction.accept(this);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
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

		Shader bgEffect = isHovered() ? GuiManager.buttonHoverBackgroundColor.getValue() : GuiManager.buttonBackgroundColor.getValue();
		Shader bdEffect = GuiManager.buttonBorderColor.getValue();

		Renderer2D renderer = Aoba.getInstance().render2D;
		renderer.beginFrame(graphics, MC.getDeltaTracker());
		renderer.drawOutlinedRoundedBox(getX(), getY(), width, height, GuiManager.roundingRadius.getValue(),
				bdEffect, bgEffect);

		Font font = GuiManager.fontSetting.getValue().getRenderer();
		int textWidth = font.width(getMessage().getString());
		int textHeight = font.lineHeight;
		int textX = getX() + (width - textWidth) / 2;
		int textY = getY() + (height - textHeight) / 2 - (int) (2 * hoverProgress) + 2;

		renderer.drawStringWithScale(getMessage().getString(), textX, textY,
				GuiManager.foregroundColor.getValue(), 1f, font);
	}
}
