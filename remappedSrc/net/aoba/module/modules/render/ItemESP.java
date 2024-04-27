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
 * ItemESP Module
 */
package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.RenderEvent;
import net.aoba.event.listeners.RenderListener;
import net.aoba.gui.colors.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

public class ItemESP extends Module implements RenderListener {

	private ColorSetting color = new ColorSetting("itemesp_color", "Color", "Color", new Color(0, 1f, 1f));
	
	
	public ItemESP() {
		super(new KeybindSetting("key.itemesp", "ItemESP Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("ItemESP");
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see items with an ESP.");
		
		this.addSetting(color);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(RenderListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(RenderListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void OnRender(RenderEvent event) {
		for (Entity entity : MC.world.getEntities()) {
			if(entity instanceof ItemEntity) {
				RenderUtils.draw3DBox(event.GetMatrixStack(), entity.getBoundingBox(), color.getValue());
			}
		}
	}

}
