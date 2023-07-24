package net.aoba.core.osettings.osettingtypes;

import net.aoba.core.osettings.OSetting;

import java.util.function.Consumer;

public class IntegerOSetting extends OSetting<Integer> {
    private final int min_value;
    private final int max_value;
    private final int step;

    public IntegerOSetting (
            String ID,
            String description,
            int default_value,
            Consumer<Integer> onUpdate,
            int min_value,
            int max_value,
            int step
    ) {
        super(ID, description, default_value, onUpdate);
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
    }

    public void increment() {
        setValue(value + step);
    }

    public void decrement() {
        setValue(value - step);
    }

    @Override
    protected boolean isValueValid(Integer value) {
        return value >= min_value && value <= max_value;
    }
}
