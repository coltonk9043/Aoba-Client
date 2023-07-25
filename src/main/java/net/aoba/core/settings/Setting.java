/*
Starts with an O as to not be confused with the legacy setting
Partially skidded from balkan hack
*/

package net.aoba.core.settings;

import net.aoba.core.utils.TextUtils;

import java.util.function.Consumer;

public abstract class Setting<T> {
    public final String ID;
    public final String name;
    public final String description;

    protected final T default_value;
    protected T value;

    public TYPE type;

    private final Consumer<T> onUpdate;

    public Setting(
            String ID,
            String description,
            T default_value,
            Consumer<T> onUpdate
    ) {
        this.ID = ID;
        this.name = TextUtils.IDToName(ID);
        this.description = description;
        this.default_value = default_value;
        this.onUpdate = onUpdate;
        this.value = default_value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (isValueValid(value)) {
            this.value = value;
        }
        update();
    }

    public void silentSetValue(T value) {
        if (isValueValid(value)) {
            this.value = value;
        }
    }

    public void resetValue() {
        setValue(default_value);
    }

    public void update() {
        if (onUpdate != null) {
            onUpdate.accept(value);
        }
    }

    public T getDefaultValue() {
        return default_value;
    }

    protected abstract boolean isValueValid(T value);

    public enum TYPE {
        BOOLEAN,
        DOUBLE,
        STRING,
        INTEGER,
        STRINGLIST,
        INDEXEDSTRINGLIST,
        VECTOR2
    }
}
