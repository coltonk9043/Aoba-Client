package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AntiKick extends Module implements TickListener {

    //Experimental

    private FloatSetting ticks;
    private FloatSetting duration;

    public int i = 0;

    public AntiKick() {
        super(new KeybindSetting("key.antikick", "AntiKick Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("AntiKick");
        this.setCategory(Category.Misc);
        this.setDescription("Avoids being kicked for 'Flying is not enabled on this Server'");

        ticks = new FloatSetting("ticks", "Ticks", "Amount of Ticks between Execution", 40f, 10f, 50f, 10f);
        duration = new FloatSetting("duration", "Duration", "Duration in Ticks", 1f, 1f, 5f, 1f);

        this.addSetting(ticks);
        this.addSetting(duration);
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
    public void OnUpdate(TickEvent event) {
        //Basically deactivates and reactivates the Flymodule in the set Period of ticks to avoid being Kicked for "Flying is not enabled on this Server"
        //For now, vertical Movement still can get you kicked if you are not carefull
        if (i == ticks.getValue() && Aoba.getInstance().moduleManager.fly.getState()) {
            Aoba.getInstance().moduleManager.fly.toggle();
        }
        if (i == ticks.getValue() + duration.getValue() && !Aoba.getInstance().moduleManager.fly.getState()) {
            Aoba.getInstance().moduleManager.fly.toggle();
            i = 0;
        }
        i++;

    }
}
