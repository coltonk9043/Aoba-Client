package net.aoba.gui.navigation;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.components.Component;
import net.aoba.misc.Render2D;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.minecraft.client.gui.DrawContext;

public class HudWindow extends Window {
	private static Color dragColor = new Color(255, 0, 0, 165);
	public BooleanSetting activated;

	public HudWindow(String ID, float x, float y, float width, float height) {
		super(ID, x, y, width, height);

		activated = new BooleanSetting(ID + "_activated", ID + " Activated", false,
				(Boolean val) -> onActivatedChanged(val));
		SettingManager.registerSetting(activated, Aoba.getInstance().settingManager.configContainer);
	}

	private void onActivatedChanged(Boolean state) {
		visible = state.booleanValue();
		Aoba.getInstance().hudManager.SetHudActive(this, state.booleanValue());
	}

	// Override to do nothing.. We want it to be visible based off of whether it is activated.
	@Override
	public void setVisible(boolean state) {
		visible = activated.getValue();
	}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (visible) {
			if (isMoving) {
				Rectangle pos = position.getValue();

				if (pos.isDrawable()) {
					float x = pos.getX().floatValue();
					float y = pos.getY().floatValue();
					float width = pos.getWidth().floatValue();
					float height = pos.getHeight().floatValue();

					Render2D.drawRoundedBox(drawContext.getMatrices().peek().getPositionMatrix(), x, y, width, height,
							GuiManager.roundingRadius.getValue(), dragColor);

					for (Component child : children) {
						child.draw(drawContext, partialTicks);
					}
				}
			}
		}
	}
}
