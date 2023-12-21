package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.gui.AbstractGui;
import net.aoba.gui.Color;
import net.aoba.gui.GuiManager;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.Vector2Setting;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.gui.DrawContext;

public class AbstractHud extends AbstractGui {

	public BooleanSetting activated;
	
	public AbstractHud(String ID, float x, float y, float width, float height) {
		super(ID, x, y, width, height);
		this.setVisible(true);
		this.activated = new BooleanSetting(ID + "_position", ID + " Activated", false, (Boolean val) -> onActivatedChanged(val));
		SettingManager.register_setting(activated, Aoba.getInstance().settingManager.config_category);
	}

	private void onActivatedChanged(Boolean state) {
		Aoba.getInstance().hudManager.SetHudActive(this, state.booleanValue());
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		
	}
}
