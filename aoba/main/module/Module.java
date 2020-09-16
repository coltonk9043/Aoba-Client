package aoba.main.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import aoba.main.misc.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public abstract class Module {
	private String name;
	private String description;
	private Category category;
	private KeyBinding keybind;
	private boolean state;
	private RenderUtils renderUtils = new RenderUtils();
	public boolean hasSettings;
	
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

	public void setBind(KeyBinding bind) {
		this.keybind = bind;
	}

	public boolean getState() {
		return this.state;
	}

	public void setState(boolean state) {
		this.onToggle();
		if (state) {
			this.onEnable();
			this.state = true;
		} else {
			this.onDisable();
			this.state = false;
		}

	}
	
	public RenderUtils getRenderUtils() {
		return this.renderUtils;
	}

	public abstract void onDisable();

	public abstract void onEnable();

	public abstract void onToggle();

	public abstract void onUpdate();

	public abstract void onRender();

	public abstract void onSendPacket(IPacket<?> packet);
	public abstract void onReceivePacket(IPacket<?> packet);
	
	public void toggle() {
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
