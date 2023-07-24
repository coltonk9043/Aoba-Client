package net.aoba.core.settings.osettingtypes;

import net.aoba.core.settings.Setting;
import net.aoba.core.utils.types.Vector2;

import java.util.function.Consumer;

public class Vector2Setting extends Setting<Vector2> {
    public Vector2Setting(
            String ID,
            String description,
            Vector2 default_value,
            Consumer<Vector2> onUpdate
    ) {
        super(ID, description, default_value, onUpdate);
        type = TYPE.VECTOR2;
    }

    public void setX(double x) {
        value.x = x;
        update();
    }

    public void setY(double y) {
        value.y = y;
        update();
    }

    public void silentSetX(double x) {
        value.x = x;
    }

    public void silentSetY(double y) {
        value.y = y;
    }

    @Override
    protected boolean isValueValid(Vector2 value) {
        return true;
    }
}
