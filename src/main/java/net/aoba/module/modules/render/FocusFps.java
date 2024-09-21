package net.aoba.module.modules.render;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FocusFps extends Module {
	private FloatSetting fps = FloatSetting.builder()
    		.id("focusfps_fps")
    		.displayName("FPS")
    		.description("The FPS for when the window is not in focus.")
    		.defaultValue(30f)
    		.minValue(1f)
    		.maxValue(45f)
    		.step(1f)
    		.build();
	
    public FocusFps() {
    	super(KeybindSetting.builder().id("key.focusfps").displayName("FocusFPS Key").defaultValue(InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)).build());

        this.setName("FocusFPS");
        this.setCategory(Category.of("Render"));
        this.setDescription("Limits the FPS of the game when it is not focused.");
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

    public Float getFps() {
        return this.fps.getValue();
    }
}
