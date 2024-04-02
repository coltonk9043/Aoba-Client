/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * Breadcrumbs Module
 */
package net.aoba.module.modules.render;

import java.util.ArrayList;
import java.util.List;
import net.aoba.Aoba;
import net.aoba.event.events.RenderEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.RenderListener;
import net.aoba.event.listeners.TickListener;
import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import net.aoba.gui.Color;
import net.aoba.misc.RainbowColor;
import net.aoba.misc.RenderUtils;

public class Breadcrumbs extends Module implements RenderListener, TickListener {
	private Color currentColor;
	
	private ColorSetting color = new ColorSetting("breadcrumbs_color", "Color",  "Color", new Color(0, 1f, 1f));
	
	private RainbowColor rainbowColor;

	public BooleanSetting rainbow = new BooleanSetting("breadcrumbs_rainbow", "Rainbow", "Rainbow", false);
	public FloatSetting effectSpeed = new FloatSetting("breadcrumbs_effectspeed", "Effect Spd.", "Effect Spd", 4f, 1f, 20f, 0.1f);
	
	private float timer = 10;
	private float currentTick = 0;
	private List<Vec3d> positions = new ArrayList<Vec3d>();
	
	public Breadcrumbs() {
		super(new KeybindSetting("key.breadcrumbs", "Breadcrumbs Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("Breadcrumbs");
		this.setCategory(Category.Render);
		this.setDescription("Shows breadcrumbs of where you last stepped;");
		currentColor = color.getValue();
		rainbowColor = new RainbowColor();
		
		this.addSetting(color);
		this.addSetting(rainbow);
		this.addSetting(effectSpeed);
	}
	
	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(RenderListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(RenderListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	@Override
	public void OnRender(RenderEvent event) {
		for(int i = 0; i < this.positions.size() - 1; i++) {
			RenderUtils.drawLine3D(event.GetMatrixStack(), this.positions.get(i), this.positions.get(i + 1), this.currentColor);
		}
	}

	@Override
	public void OnUpdate(TickEvent event) {
		currentTick++;
		if(timer == currentTick) {
			currentTick = 0;
			if(!Aoba.getInstance().moduleManager.freecam.getState()) {
				positions.add(MC.player.getPos());
			}
		}
		if(this.rainbow.getValue()) {
			this.rainbowColor.update(this.effectSpeed.getValue().floatValue());
			this.currentColor = this.rainbowColor.getColor();
		}else {
			this.currentColor = color.getValue();
		}
	}
}