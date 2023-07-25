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
 * ChestESP Module
 */
package net.aoba.module.modules.render;

import java.util.ArrayList;
import java.util.stream.Collectors;

import net.aoba.core.settings.osettingtypes.BooleanSetting;
import net.aoba.core.settings.osettingtypes.DoubleSetting;
import org.lwjgl.glfw.GLFW;
import net.aoba.gui.Color;
import net.aoba.misc.ModuleUtils;
import net.aoba.misc.RainbowColor;
import net.aoba.module.Module;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Box;

public class ChestESP extends Module {
	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	public DoubleSetting hue = new DoubleSetting("chestesp_hue", "Hue", 4, null, 0, 360, 1);
	public BooleanSetting rainbow = new BooleanSetting("chestesp_rainbow", "Rainbow", false, null);
	public DoubleSetting effectSpeed = new DoubleSetting("chestesp_effectspeed", "Effect Speed", 4, null, 1, 20, 0.1);
	
	public ChestESP() {
		this.setName("ChestESP");
		this.setBind(new KeyBinding("key.chestesp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see Chests with an ESP.");
		color = new Color(hue.getValue().floatValue(), 1f, 1f);
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
		if(this.rainbow.getValue()) {
			this.rainbowColor.update(this.effectSpeed.getValue().floatValue());
			this.currentColor = this.rainbowColor.getColor();
		}else {
			this.color.setHSV(hue.getValue().floatValue(), 1f, 1f);
			this.currentColor = color;
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		ArrayList<BlockEntity> blockEntities = ModuleUtils.getTileEntities().collect(Collectors.toCollection(ArrayList::new));
		for(BlockEntity blockEntity : blockEntities) {
			if(blockEntity instanceof ChestBlockEntity || blockEntity instanceof TrappedChestBlockEntity) {
				Box box = new Box(blockEntity.getPos());
				this.getRenderUtils().draw3DBox(matrixStack, box, currentColor, 0.2f);
			}
		}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}

}
