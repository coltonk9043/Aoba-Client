package aoba.main.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.ibm.icu.impl.ICUService.Key;

import aoba.main.misc.RenderUtils;
import aoba.main.settings.Setting;
import aoba.main.settings.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.IPacket;

public abstract class Module {
	private String name;
	private String description;
	private Category category;
	private KeyBinding keybind;
	private boolean state;
	private RenderUtils renderUtils = new RenderUtils();

	private List<Setting> settings = new ArrayList<Setting>();
	
	public Minecraft mc = Minecraft.getInstance();
	
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
			this.keybind = new KeyBinding(bind.getKeyDescription(), InputMappings.getInputByName(Settings.getSettingString(bind.getKeyDescription())).getKeyCode(), bind.getKeyCategory());
		} catch (Exception e) {
			this.keybind = bind;
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

	public abstract void onRender();

	public abstract void onSendPacket(IPacket<?> packet);
	public abstract void onReceivePacket(IPacket<?> packet);
	
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
