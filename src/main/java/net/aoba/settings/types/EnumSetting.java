package net.aoba.settings.types;

import net.aoba.settings.Setting;

import java.util.function.Consumer;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {

    private T[] enumConstants;

    public EnumSetting(String ID, String description, T defaultValue) {
        super(ID, description, defaultValue);
        this.enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
        type = TYPE.ENUM;
    }

    public EnumSetting(String ID, String displayName, String description, T defaultValue) {
        super(ID, displayName, description, defaultValue);
        this.enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
        type = TYPE.ENUM;
    }

    public EnumSetting(String ID, String description, T defaultValue, Consumer<T> onUpdate) {
        super(ID, description, defaultValue, onUpdate);
        this.enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
        type = TYPE.ENUM;
    }

    /**
     * Setter for the value. Validates if the value is an instance of the enum constants.
     */
    @Override
    public void setValue(T value) {
        for (T constant : enumConstants) {
            if (constant.equals(value)) {
                super.setValue(value);
                return;
            }
        }
        throw new IllegalArgumentException("Invalid enum value: " + value);
    }

    /**
     * Checks whether or not a value is within this setting's valid range.
     * In this context, it checks if the value is a valid enum constant.
     */
    @Override
    protected boolean isValueValid(T value) {
        for (T constant : enumConstants) {
            if (constant.equals(value)) {
                return true;
            }
        }
        return false;
    }
}