package net.aoba.gui.navigation;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.components.Component;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;

public class HudWindow extends Window {
	private static Color hoverColor = new Color(255, 0, 0);
	private static Color dragColor = new Color(255, 0, 0, 165);
	public BooleanSetting activated;

	public CloseableWindow optionsWindow;
	
	public HudWindow(String ID, float x, float y, float width, float height) {
		super(ID, x, y, width, height);

		activated = new BooleanSetting(ID + "_activated", ID + " Activated", false, (Boolean val) -> onActivatedChanged(val));
		SettingManager.registerSetting(activated, Aoba.getInstance().settingManager.configContainer);
	}

	private void onActivatedChanged(Boolean state) {
		Aoba.getInstance().guiManager.SetHudActive(this, state.booleanValue());
	}

	@Override
	public boolean getVisible() {
		return activated.getValue().booleanValue();
	}
	
	
	// Override to do nothing.. We want it to be visible based off of whether it is activated.
	@Override
	public void setVisible(boolean state) {
		if(!state) {
			isMouseOver = false;
			isMoving = false;
		}
	}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (getVisible()) {
			Rectangle pos = position.getValue();
			
			float x = pos.getX().floatValue();
			float y = pos.getY().floatValue();
			float width = pos.getWidth().floatValue();
			float height = pos.getHeight().floatValue();
			
			if (isMoving) {
				if (pos.isDrawable()) {
					Render2D.drawRoundedBox(drawContext.getMatrices().peek().getPositionMatrix(), x, y, width, height,
							GuiManager.roundingRadius.getValue(), dragColor);
				}
			}
			if(Aoba.getInstance().guiManager.isClickGuiOpen() && isMouseOver) {
				Render2D.drawRoundedBoxOutline(drawContext.getMatrices().peek().getPositionMatrix(), x, y, width, height,
						GuiManager.roundingRadius.getValue(), hoverColor);
			}
			
			for (Component child : children) {
				child.draw(drawContext, partialTicks);
			}
		}
	}
}
