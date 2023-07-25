package net.aoba.core.settings.osettingtypes;

import net.aoba.core.settings.Setting;

import java.util.function.Consumer;

public class IntegerSetting extends Setting<Integer> {
    public final int min_value;
    public final int max_value;
    public final int step;

    public IntegerSetting(
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
        type = TYPE.INTEGER;
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
