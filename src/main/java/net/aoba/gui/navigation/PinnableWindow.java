package net.aoba.gui.navigation;

import org.joml.Matrix4f;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class PinnableWindow extends Window {
	protected BooleanSetting isPinned;

	public PinnableWindow(String ID, float x, float y) {
		super(ID, x, y);

		isPinned = BooleanSetting.builder().id(ID + "_pinned").defaultValue(false).build();

		SettingManager.registerSetting(isPinned);
	}

	public final boolean isPinned() {
		return isPinned.getValue();
	}

	public final void setPinned(boolean pin) {
		this.isPinned.setValue(pin);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		// Check to see if the event is cancelled. If not, execute branch.
		if (!event.isCancelled()) {
			if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				Rectangle pos = position.getValue();

				Rectangle pinHitbox = new Rectangle(pos.getX() + pos.getWidth() - 24, pos.getY() + 4, 16.0f, 16.0f);
				if (pinHitbox.intersects(mouseX, mouseY)) {
					this.isPinned.setValue(!isPinned.getValue());
					event.cancel();
					return;
				}
			}
		}

		// Cancel any other movements if it is pinned.
		if (this.isPinned())
			event.cancel();

		super.onMouseClick(event);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		Rectangle pos = position.getValue();
		if (pos.isDrawable()) {
			float x = pos.getX().floatValue();
			float y = pos.getY().floatValue();
			float width = pos.getWidth().floatValue();

			if (this.isPinned.getValue()) {
				Render2D.drawRoundedBox(matrix4f, x + width - 23, y + 8, 15, 15, GuiManager.roundingRadius.getValue(),
						new Color(154, 0, 0, 200));
				Render2D.drawRoundedBoxOutline(matrix4f, x + width - 23, y + 8, 15, 15,
						GuiManager.roundingRadius.getValue(), new Color(0, 0, 0, 200));
			} else {
				Render2D.drawRoundedBox(matrix4f, x + width - 23, y + 8, 15, 15, GuiManager.roundingRadius.getValue(),
						new Color(128, 128, 128, 50));
				Render2D.drawRoundedBoxOutline(matrix4f, x + width - 23, y + 8, 15, 15,
						GuiManager.roundingRadius.getValue(), new Color(0, 0, 0, 50));
			}
		}
	}
}
