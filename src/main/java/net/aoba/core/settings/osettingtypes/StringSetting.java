package net.aoba.core.settings.osettingtypes;

import net.aoba.core.settings.Setting;

import java.util.function.Consumer;

public class StringSetting extends Setting<String> {
    public StringSetting(
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
