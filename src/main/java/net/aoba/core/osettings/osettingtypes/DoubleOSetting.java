package net.aoba.core.osettings.osettingtypes;

import net.aoba.core.osettings.OSetting;

import java.util.function.Consumer;

public class DoubleOSetting extends OSetting<Double> {
    public final double min_value;
    public final double max_value;
    public final double step;

    public DoubleOSetting(
            String ID,
            String description,
            double default_value,
            Consumer<Double> onUpdate,
            double min_value,
            double max_value,
            double step
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
    protected boolean isValueValid(Double value) { return value >= min_value && value <= max_value; }
}
