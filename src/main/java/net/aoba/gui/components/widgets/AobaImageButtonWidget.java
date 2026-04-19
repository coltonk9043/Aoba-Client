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
import net.aoba.gui.colors.Colors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class AobaImageButtonWidget extends AbstractButton {
	private Consumer<AobaImageButtonWidget> pressAction;
	private final Identifier image;
	private boolean background = true;
	private static final Shader IMAGE_SHADER = Shader.solid(Colors.White);

	public AobaImageButtonWidget(int x, int y, int width, int height, Identifier image) {
		super(x, y, width, height, Component.empty());

		this.image = image;
	}

	public AobaImageButtonWidget(int x, int y, int width, int height, Identifier image,
			boolean background) {
		super(x, y, width, height, Component.empty());

		this.image = image;
		this.background = background;
	}

	public void setPressAction(Consumer<AobaImageButtonWidget> pressAction) {
		this.pressAction = pressAction;
	}

	@Override
	public void onPress(InputWithModifiers input) {
		if (pressAction != null) {
			pressAction.accept(this);
		}
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (background) {
			Shader bgEffect = isHovered() ? GuiManager.buttonHoverBackgroundColor.getValue()
					: GuiManager.buttonBackgroundColor.getValue();
			Shader bdEffect = GuiManager.buttonBorderColor.getValue();

			Renderer2D renderer = Aoba.getInstance().render2D;
			renderer.beginFrame(graphics, MC.getDeltaTracker());
			renderer.drawOutlinedRoundedBox(getX(), getY(), width, height,
					GuiManager.roundingRadius.getValue(), bdEffect, bgEffect);
			renderer.drawTexturedQuad(image, getX(), getY(), width, height, IMAGE_SHADER);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		
	}
}