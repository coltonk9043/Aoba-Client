/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.event.events.RenderEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.RenderListener;
import net.aoba.event.listeners.TickListener;
import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Vec3d;
import net.aoba.gui.Color;
import net.aoba.misc.RainbowColor;

public class Breadcrumbs extends Module implements RenderListener, TickListener {
	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	public FloatSetting hue = new FloatSetting("breadcrumbs_hue", "Hue", "Hue", 4, 0, 360, 1);
	public BooleanSetting rainbow = new BooleanSetting("breadcrumbs_rainbow", "Rainbow", "Rainbow", false);
	public FloatSetting effectSpeed = new FloatSetting("breadcrumbs_effectspeed", "Effect Spd.", "Effect Spd", 4, 1, 20, 0.1);
	
	private float timer = 10;
	private float currentTick = 0;
	private List<Vec3d> positions = new ArrayList<Vec3d>();
	
	public Breadcrumbs() {
		this.setName("Breadcrumbs");
		this.setBind(new KeyBinding("key.breadcrumbs", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Shows breadcrumbs of where you last stepped;");
		color = new Color(0f, 1f, 1f);
		currentColor = color;
		rainbowColor = new RainbowColor();
		this.addSetting(hue);
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
			this.getRenderUtils().drawLine3D(event.GetMatrixStack(), this.positions.get(i), this.positions.get(i + 1), this.currentColor);
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
			this.color.setHSV(hue.getValue().floatValue(), 1f, 1f);
			this.currentColor = color;
		}
	}
}