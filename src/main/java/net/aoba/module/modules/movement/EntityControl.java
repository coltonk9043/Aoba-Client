package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.PostTickEvent;
import net.aoba.event.listeners.PostTickListener;
import net.aoba.interfaces.IHorseBaseEntity;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.lwjgl.glfw.GLFW;

public class EntityControl extends Module implements PostTickListener {
    public EntityControl() {
        super(new KeybindSetting("key.entitycontrol", "EntityControl Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("EntityControl");
        this.setDescription("Allows you to control entities without needing a saddle.");
        this.setCategory(Category.of("Movement"));
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(PostTickListener.class, this);

        for (Entity entity : MC.world.getEntities()) {
            if (entity instanceof AbstractHorseEntity) ((IHorseBaseEntity) entity).setSaddled(false);
        }
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(PostTickListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void onPostTick(PostTickEvent event) {
        for (Entity entity : MC.world.getEntities()) {
            if (entity instanceof AbstractHorseEntity) ((IHorseBaseEntity) entity).setSaddled(true);
        }
    }
}
