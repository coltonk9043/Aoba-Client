package net.aoba.core.settings.osettingtypes;

import net.aoba.core.settings.Setting;

import java.util.function.Consumer;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(
            String ID,
            String description,
            boolean default_value,
            Consumer<Boolean> onUpdate
    ) {
        super(ID, description, default_value, onUpdate);
        type = TYPE.BOOLEAN;
    }

    public void toggle() {
        setValue(!value);
    }

    @Override
    protected boolean isValueValid(Boolean value) { return true; }
}
