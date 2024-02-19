package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.hud.AbstractHud;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public class HudComponent extends Component implements LeftMouseDownListener {
	private String text;
	private AbstractHud hud;

	public HudComponent(String text, IGuiElement parent, AbstractHud hud) {
		super(parent);
		this.text = text;
		this.hud = hud;
		
		this.setHeight(30);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
		RenderUtils.drawString(drawContext, this.text, actualX + 8, actualY + 8, 0xFFFFFF);
		
		if(this.hud.activated.getValue()) {
			RenderUtils.drawString(drawContext, "-", actualX + actualWidth - 16, actualY + 8, 0xFF0000);
		}else {
			RenderUtils.drawString(drawContext, "+", actualX + actualWidth - 16, actualY + 8, 0x00FF00);
		}
	}
	
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		if(this.hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
			boolean visibility = hud.activated.getValue();
			Aoba.getInstance().hudManager.SetHudActive(hud, !visibility);
		}
	}
	
	@Override
	public void OnVisibilityChanged() {
		if(this.isVisible()) {
			Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		}else {
			Aoba.getInstance().eventManager.RemoveListener(LeftMouseDownListener.class, this);
		}
	}
}