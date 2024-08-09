package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class EXPThrower extends Module implements TickListener {
    private FloatSetting pitchSetting;
    private BooleanSetting autoSwapSetting;
    private FloatSetting throwDelaySetting;
    private BooleanSetting autoToggleSetting;

    private long lastThrowTime = 0;

    public EXPThrower() {
        super(new KeybindSetting("key.expthrower", "EXPThrower Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("EXPThrower");
        this.setCategory(Category.of("misc"));
        this.setDescription("Automatically uses XP bottles.");

        pitchSetting = new FloatSetting("pitch", "Pitch", "The pitch angle for throwing XP bottles.", 90.0f, 0.0f, 90.0f, 1.0f);
        autoSwapSetting = new BooleanSetting("auto_swap", "Auto Swap", "Automatically swap to XP bottles if not in hand.", true);
        throwDelaySetting = new FloatSetting("throw_delay", "Throw Delay", "Delay between throws in ticks.", 20, 1, 100, 1);
        autoToggleSetting = new BooleanSetting("auto_toggle", "Auto Toggle", "Automatically toggle off when no XP bottles are found.", true);

        this.addSetting(pitchSetting);
        this.addSetting(autoSwapSetting);
        this.addSetting(throwDelaySetting);
        this.addSetting(autoToggleSetting);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);

        lastThrowTime = 0;
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastThrowTime < throwDelaySetting.getValue() * 50) {
            return;
        }

        FindItemResult exp = findInHotbar(Items.EXPERIENCE_BOTTLE);
        if (!exp.found()) {
            if (autoToggleSetting.getValue()) {
                toggle();
            }
            return;
        }

        rotatePitch(pitchSetting.getValue());

        if (exp.getHand() != null) {
            MC.interactionManager.interactItem(MC.player, exp.getHand());
        } else if (autoSwapSetting.getValue()) {
            swap(exp.slot(), true);
            MC.interactionManager.interactItem(MC.player, exp.getHand());
            swapBack();
        }

        lastThrowTime = currentTime;
    }
}