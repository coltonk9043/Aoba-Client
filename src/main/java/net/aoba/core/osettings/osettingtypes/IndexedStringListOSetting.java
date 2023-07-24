package net.aoba.core.osettings.osettingtypes;

import java.util.List;
import java.util.function.Consumer;

public class IndexedStringListOSetting extends StringListOSetting {
    public int index = 0;

    public IndexedStringListOSetting(String ID, String description, List<String> default_value, Consumer<List<String>> onUpdate) {
        super(ID, description, default_value, onUpdate);
    }

    public String getIndexValue() {
        return value.get(index);
    }

    public void increment() {
        index += 1;
        if (index > value.size()) {
            index = 0;
        }
    }

    public void decrement() {
        index -= 1;
        if (index < 0) {
            index = value.size() - 1;
        }
    }
}
