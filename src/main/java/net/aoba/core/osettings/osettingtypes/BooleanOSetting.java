package net.aoba.core.osettings.osettingtypes;

import net.aoba.core.osettings.OSetting;

import java.util.function.Consumer;

public class BooleanOSetting extends OSetting<Boolean> {
    public BooleanOSetting (
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
