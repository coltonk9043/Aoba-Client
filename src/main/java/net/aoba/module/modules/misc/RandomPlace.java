package net.aoba.module.modules.misc;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.HotbarSetting;

import java.util.ArrayList;
import java.util.Collections;

public class RandomPlace extends Module {
    public final HotbarSetting allowed_slots = HotbarSetting.builder()
            .id("randomplace_allowed_slots")
            .displayName("Allowed Slots")
            .description("Which slots of the hotbar should be used")
            .defaultValue(new ArrayList<>(Collections.nCopies(9, false)))
            .build();

    public RandomPlace() {
        super("RandomPlace");

        setCategory(Category.of("Misc"));
        setDescription("Place a random block from the hotbar.");

        addSetting(allowed_slots);
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

    public HotbarSetting getAllowdSlots() {
        return allowed_slots;
    }
}
