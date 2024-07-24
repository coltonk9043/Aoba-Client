package net.aoba.module.modules.render;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Tooltips extends Module {
    public BooleanSetting storage = new BooleanSetting("tooltips_storage", "Storage", "Renders the contents of the storage item.", true);
    public BooleanSetting maps = new BooleanSetting("tooltips_maps", "Maps", "Render a map preview", true);

    public Tooltips() {
        super(new KeybindSetting("key.tooltips", "Tooltips Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

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
