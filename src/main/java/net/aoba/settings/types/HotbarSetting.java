package net.aoba.settings.types;

import net.aoba.settings.Setting;
import net.minecraft.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class HotbarSetting extends Setting<List<Boolean>> {
    protected HotbarSetting(String ID, String displayName, String description, List<Boolean> default_value, Consumer<List<Boolean>> onUpdate) {
        super(ID, displayName, description, default_value, onUpdate);
        type = TYPE.HOTBAR;
    }

    @Override
    protected boolean isValueValid(List<Boolean> value) {
        return value.size() == 9;
    }

    public void setValueAt(int index, Boolean value) {
        this.value.set(index, value);
    }

    public Boolean getValueAt(int index) {
        return this.value.get(index);
    }

    public static HotbarSetting.BUILDER builder() {
        return new HotbarSetting.BUILDER();
    }

    public static class BUILDER extends Setting.BUILDER<HotbarSetting.BUILDER, HotbarSetting, List<Boolean>> {
        protected BUILDER() {
        }

        @Override
        public HotbarSetting build() {
            return new HotbarSetting(id, displayName, description, defaultValue, onUpdate);
        }
    }
}
