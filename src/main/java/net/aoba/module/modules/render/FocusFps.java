package net.aoba.module.modules.render;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FocusFps extends Module {
    private FloatSetting fps = new FloatSetting("focusfps_fps", "FPS", "The FPS for when the window is not in focus.", 30, 1, 45, 1);

    public FocusFps() {
        super(new KeybindSetting("key.focusfps", "FocusFPS Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

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
