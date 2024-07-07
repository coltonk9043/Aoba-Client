package net.aoba.module.modules.movement;

import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HighJump extends Module {

    private FloatSetting multiplier;

    public HighJump() {
        super(new KeybindSetting("key.higheump", "Higher Jump Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("HighJump");
        this.setCategory(Category.Movement);
        this.setDescription("Allows the player to jump super high!");

        multiplier = new FloatSetting("highjump.jumpmultiplier", "Jump Multiplier", "The height that the player will jump.", 1.5f, 0.1f, 10.0f, 0.1f);

        this.addSetting(multiplier);
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

    public float getJumpHeightMultiplier() {
        return multiplier.getValue();
    }
}