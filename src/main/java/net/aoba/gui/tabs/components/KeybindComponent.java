package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.core.settings.types.KeybindSetting;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.IHudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;

public class KeybindComponent extends Component implements LeftMouseDownListener, KeyDownListener {
	private boolean listeningForKey;
	private KeybindSetting keyBind;
	
	public KeybindComponent(IHudElement parent, KeybindSetting keyBind) {
		super(parent);
		this.keyBind = keyBind;
		
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
	}

	@Override
	public void update() {}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
		renderUtils.drawString(drawContext, "Keybind", actualX + 8, actualY + 8, 0xFFFFFF);
		renderUtils.drawBox(drawContext.getMatrices(), actualX + actualWidth - 100, actualY + 2, 98, actualHeight - 4, new Color(115, 115, 115), 0.8f);
		renderUtils.drawOutline(drawContext.getMatrices(), actualX + actualWidth - 100, actualY + 2, 98, actualHeight - 4);
		renderUtils.drawString(drawContext, this.keyBind.getValue().getBoundKeyLocalizedText().getString(), actualX + actualWidth - 90, actualY + 8, 0xFFFFFF);
	}

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		if (hovered) {
			listeningForKey = !listeningForKey;
		}
	}

	@Override
	public void OnKeyDown(KeyDownEvent event) {
		if(listeningForKey) {
			int key = event.GetKey();
			int scanCode = event.GetScanCode();
			
			keyBind.getValue().setBoundKey(InputUtil.fromKeyCode(key, scanCode));
			listeningForKey = false;
			
			event.SetCancelled(true);
		}
	}
}