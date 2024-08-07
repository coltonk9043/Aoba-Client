package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class EXPThrower extends Module implements TickListener {
    public EXPThrower() {
        super(new KeybindSetting("key.expthrower", "EXPThrower Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("EXPThrower");
        this.setCategory(Category.of("misc"));
        this.setDescription("Automatically uses XP bottles.");
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
        FindItemResult exp = findInHotbar(Items.EXPERIENCE_BOTTLE);
        if (!exp.found()) return;

        rotatePitch(90);

        if (exp.getHand() != null) {
            MC.interactionManager.interactItem(MC.player, exp.getHand());
        }

        else {
            swap(exp.slot(), true);
            MC.interactionManager.interactItem(MC.player, exp.getHand());
            swapBack();
        }
    }
}
