package net.aoba.module.modules.render;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Tooltips extends Module {
	
	public BooleanSetting storage = BooleanSetting.builder()
    		.id("tooltips_storage")
    		.displayName("Storage")
    		.description("Renders the contents of the storage item.")
    		.defaultValue(true)
    		.build();
	
	public BooleanSetting maps = BooleanSetting.builder()
    		.id("tooltips_maps")
    		.displayName("Maps")
    		.description("Render a map preview")
    		.defaultValue(true)
    		.build();
	
    public Tooltips() {
    	super(KeybindSetting.builder().id("key.tooltips").displayName("Tooltips Key").defaultValue(InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)).build());

        this.setName("Tooltips");
        this.setCategory(Category.of("Render"));
        this.setDescription("Renders custom item tooltips");

        this.addSetting(storage);
        this.addSetting(maps);
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

    public Boolean getStorage() {
        return this.storage.getValue();
    }

    public boolean getMap() {
        return this.maps.getValue();
    }
}
