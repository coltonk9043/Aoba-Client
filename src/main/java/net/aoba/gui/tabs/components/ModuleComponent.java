package net.aoba.gui.tabs.components;

import org.joml.Matrix4f;
import com.mojang.blaze3d.systems.RenderSystem;
import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.module.Module;
import net.aoba.gui.Color;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.tabs.ModuleSettingsTab;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ModuleComponent extends Component implements LeftMouseDownListener {
	private String text;
	private Module module;

	private ModuleSettingsTab lastSettingsTab = null;
	
	public final Identifier gear;
	
	public ModuleComponent(String text, IGuiElement parent, Module module) {
		super(parent);
		
		gear = new Identifier("aoba", "/textures/gear.png");
		this.text = text;
		this.module = module;
		
		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
		RenderUtils.drawString(drawContext, this.text, actualX + 8, actualY + 8, module.getState() ? 0x00FF00 : this.hovered ? color.getColorAsInt() : 0xFFFFFF);
		if(module.hasSettings()) {
			Color hudColor = Aoba.getInstance().hudManager.color.getValue();
			RenderUtils.drawTexturedQuad(drawContext, gear, (actualX + actualWidth - 20), (actualY + 6), 16, 16, hudColor);
		}
	}
	
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = event.GetMouseX();
		if (hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
				boolean isOnOptionsButton = (mouseX >= (actualX + actualWidth - 34) && mouseX <= (actualX + actualWidth));
				if (isOnOptionsButton) {
					if(lastSettingsTab == null) {
						lastSettingsTab = new ModuleSettingsTab(this.module.getName(), this.actualX + this.actualWidth + 1, this.actualY, this.module);
						lastSettingsTab.setVisible(true);
						Aoba.getInstance().hudManager.AddHud(lastSettingsTab, "Modules");
					}else {
						Aoba.getInstance().hudManager.RemoveHud(lastSettingsTab, "Modules");
						lastSettingsTab = null;
					}
				} else {
					module.toggle();
					return;
				}
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
