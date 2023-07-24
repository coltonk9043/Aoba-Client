package net.aoba.core.settings.osettingtypes;

import java.util.List;
import java.util.function.Consumer;

public class IndexedStringListSetting extends StringListSetting {
    public int index = 0;

    public IndexedStringListSetting(String ID, String description, List<String> default_value, Consumer<List<String>> onUpdate) {
        super(ID, description, default_value, onUpdate);
        type = TYPE.INDEXEDSTRINGLIST;
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
