package net.aoba.core.osettings.osettingtypes;

import net.aoba.core.osettings.OSetting;
import net.aoba.core.utils.types.Vector2;

import java.util.function.Consumer;

public class Vector2OSetting extends OSetting<Vector2> {
    public Vector2OSetting (
            String ID,
            String description,
            Vector2 default_value,
            Consumer<Vector2> onUpdate
    ) {
        super(ID, description, default_value, onUpdate);
    }

    @Override
    protected boolean isValueValid(Vector2 value) {
        return true;
    }
}
