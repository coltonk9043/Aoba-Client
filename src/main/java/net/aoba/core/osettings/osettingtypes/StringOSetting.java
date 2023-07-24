package net.aoba.core.osettings.osettingtypes;

import net.aoba.core.osettings.OSetting;

import java.util.function.Consumer;

public class StringOSetting extends OSetting<String> {
    public StringOSetting (
            String ID,
            String description,
            String default_value,
            Consumer<String> onUpdate
    ) {
        super(ID, description, default_value, onUpdate);
        type = TYPE.STRING;
    }

    @Override
    protected boolean isValueValid(String value) {
        return true;
    }
}
