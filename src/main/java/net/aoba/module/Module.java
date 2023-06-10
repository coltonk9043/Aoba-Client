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
 * A class to represent a generic module.
 */
package net.aoba.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import net.aoba.AobaClient;
import net.aoba.interfaces.IMinecraftClient;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;

public abstract class Module {
	private String name;
	private String description;
	private Category category;
	private KeyBinding keybind;
	private boolean state;
	private RenderUtils renderUtils = new RenderUtils();

	private List<Setting> settings = new ArrayList<Setting>();
	
	protected static final MinecraftClient MC = AobaClient.MC;
	protected static final IMinecraftClient IMC = AobaClient.IMC;
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Module.Category getCategory() {
		return this.category;
	}

	public void setCategory(Module.Category category) {
		this.category = category;
	}

	public KeyBinding getBind() {
		return this.keybind;
	}


	public boolean getState() {
		return this.state;
	}

	public void setState(boolean state) {
		this.onToggle();
		if(this.state = state) return;
		if (state) {
			this.onEnable();
			this.state = true;
		} else {
			this.onDisable();
			this.state = false;
		}

	}

	public void setBind(KeyBinding bind) {
		try {
			this.keybind = bind;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void addSetting(Setting setting) {
		this.settings.add(setting);
	}
	
	public List<Setting> getSettings() {
		return this.settings;
	}
	
	public RenderUtils getRenderUtils() {
		return this.renderUtils;
	}
	
	public boolean hasSettings() {
		return !this.settings.isEmpty();
	}

	public abstract void onDisable();

	public abstract void onEnable();

	public abstract void onToggle();

	public abstract void onUpdate();

	public abstract void onRender(MatrixStack matrixStack, float partialTicks);

	public abstract void onSendPacket(Packet<?> packet);
	public abstract void onReceivePacket(Packet<?> packet);
	
	public void toggle() {
		if(this.state) {
			this.onDisable();
		}else {
			this.onEnable();
		}
		this.setState(!this.getState());
	}

	public final boolean isCategory(Module.Category category) {
		return category == this.category;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ModInfo {
		String name();

		String description();

		Module.Category category();

		int bind();
	}

	public static enum Category {
		Combat(), Movement(), Render(), World(), Misc();
		Module module;
		
		private Category() {
			
		}
	}
}
