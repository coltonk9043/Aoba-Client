/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components.widgets;

import java.util.function.Consumer;

import net.aoba.gui.GuiManager;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class AobaImageButtonWidget extends AbstractButton {
	private Consumer<AobaImageButtonWidget> pressAction;
	private final Identifier image;
	private int u = 0;
	private int v = 0;
	private boolean background = true;

	public AobaImageButtonWidget(int x, int y, int width, int height, Identifier image) {
		super(x, y, width, height, Component.empty());

		this.image = image;
	}

	public AobaImageButtonWidget(int x, int y, int u, int v, int width, int height, Identifier image) {
		super(x, y, width, height, Component.empty());

		this.image = image;
		this.u = u;
		this.v = v;
	}

	public AobaImageButtonWidget(int x, int y, int u, int v, int width, int height, Identifier image,
			boolean background) {
		super(x, y, width, height, Component.empty());

		this.image = image;
		this.u = u;
		this.v = v;
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
	protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (background) {
			Render2D.setup();
			try {
				Render2D.drawOutlinedRoundedBox(context, getX(), getY(), width, height,
						GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue(),
						GuiManager.backgroundColor.getValue());
			} finally {
				Render2D.end();
			}
		}

		context.blit(RenderPipelines.GUI_TEXTURED, image, getX(), getY(), (float) u, (float) v, width, height, width, height);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
	}
}