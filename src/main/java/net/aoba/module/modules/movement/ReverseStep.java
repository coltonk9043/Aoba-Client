package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ReverseStep extends Module implements TickListener {
    public ReverseStep() {
        super(new KeybindSetting("key.reversestep", "ReverseStep Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("ReverseStep");
        this.setCategory(Category.of("Movement"));
        this.setDescription("Steps. But in reverse...");
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle() {

    }

	@Override
	public void onTick(Pre event) {
	       if (MC.player.isOnGround()) {
	            MC.player.setVelocity(MC.player.getVelocity().x, MC.player.getVelocity().y - 1.0, MC.player.getVelocity().z);
	        }
	}

	@Override
	public void onTick(Post event) {

	}
}
