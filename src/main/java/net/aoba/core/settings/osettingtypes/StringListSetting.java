package net.aoba.core.settings.osettingtypes;

import net.aoba.core.settings.Setting;

import java.util.List;
import java.util.function.Consumer;

public class StringListSetting extends Setting<List<String>> {
    public StringListSetting(
            String ID,
            String description,
            List<String> default_value,
            Consumer<List<String>> onUpdate
    ) {
        super(ID, description, default_value, onUpdate);
        type = TYPE.STRINGLIST;
    }

    public void appendString(String value) {
        this.value.add(value);
        update();
    }

    public void removeString(int index) {
        if (index >= 0 && index < value.size()) {
            value.remove(index);
            update();
        }
        // TODO: add out of bounds error .. maybe
    }

    public String getValueAt(int index) {
        if (index >= 0 && index < value.size()) {
            return value.get(index);
        }
        return null;
        // TODO: add out of bounds error .. maybe
    }

    @Override
    protected boolean isValueValid(List<String> value) {
        return true;
    }
}
