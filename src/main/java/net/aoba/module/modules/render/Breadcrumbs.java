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
import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;
import net.aoba.gui.Color;
import net.aoba.misc.RainbowColor;

public class Breadcrumbs extends Module{
	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	public SliderSetting hue = new SliderSetting("Hue", "breadcrumbs_hue", 4, 0, 360, 1);
	public BooleanSetting rainbow = new BooleanSetting("Rainbow", "breadcrumbs_rainbow");
	public SliderSetting effectSpeed = new SliderSetting("Effect Spd", "breadcrumbs_effectspeed", 4, 1, 20, 0.1);
	
	private float timer = 10;
	private float currentTick = 0;
	private List<Vec3d> positions = new ArrayList<Vec3d>();
	
	public Breadcrumbs() {
		this.setName("Breadcrumbs");
		this.setBind(new KeyBinding("key.breadcrumbs", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Shows breadcrumbs of where you last stepped;");
		color = new Color(hue.getValueFloat(), 1f, 1f);
		currentColor = color;
		rainbowColor = new RainbowColor();
		this.addSetting(hue);
		this.addSetting(rainbow);
		this.addSetting(effectSpeed);
	}
	
	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onToggle() {

	}
 
	@Override
	public void onUpdate() {
		currentTick++;
		if(timer == currentTick) {
			currentTick = 0;
			positions.add(MC.player.getPos());
		}
		if(this.rainbow.getValue()) {
			this.rainbowColor.update(this.effectSpeed.getValueFloat());
			this.currentColor = this.rainbowColor.getColor();
		}else {
			this.color.setHSV(hue.getValueFloat(), 1f, 1f);
			this.currentColor = color;
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		for(int i = 0; i < this.positions.size() - 1; i++) {
			this.getRenderUtils().drawLine3D(matrixStack, this.positions.get(i), this.positions.get(i + 1), this.currentColor);
		}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}