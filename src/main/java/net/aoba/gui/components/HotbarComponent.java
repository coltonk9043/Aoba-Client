package net.aoba.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.aoba.gui.GuiManager;
import net.aoba.gui.VerticalAlignment;
import net.aoba.gui.colors.Color;
import net.aoba.gui.components.StackPanelComponent.StackType;
import net.aoba.settings.types.HotbarSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class HotbarComponent extends Component {
    private static final Color COLOR_ON = new Color(0, 154, 0, 200);
    private static final Color COLOR_OFF = new Color(154, 0, 0, 200);

    private List<Boolean> values;
    private HotbarSetting hotbar;
    private Consumer<List<Boolean>> onChanged;
    private final RectangleComponent[] slotComponents;

    private HotbarComponent(String text, List<Boolean> items) {
        this.values = new ArrayList<>(items);
    	StackPanelComponent verticalStack = new StackPanelComponent();
        verticalStack.setSpacing(4f);

        StringComponent label = new StringComponent(text);
        label.setIsHitTestVisible(false);
        verticalStack.addChild(label);

        StackPanelComponent slotsRow = new StackPanelComponent();
        slotsRow.setDirection(StackType.Horizontal);
        slotsRow.setSpacing(4f);

        slotComponents = new RectangleComponent[values.size()];
        for (int i = 0; i < values.size(); i++) {
            final int slotIndex = i;
            RectangleComponent slot = new RectangleComponent(
                    values.get(i) ? COLOR_ON : COLOR_OFF,
                    GuiManager.borderColor.getValue(),
                    3f);
            slot.setWidth(20f);
            slot.setHeight(20f);
            slot.setVerticalAlignment(VerticalAlignment.Center);
            slot.setOnClicked(e -> {
                if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
                    toggleSlot(slotIndex);
                    e.cancel();
                }
            });
            slotComponents[slotIndex] = slot;
            slotsRow.addChild(slot);
        }

        verticalStack.addChild(slotsRow);
        addChild(verticalStack);
    }
    
    public HotbarComponent(String text, List<Boolean> values, Consumer<List<Boolean>> onChanged) {
    	this(text, values);
        this.onChanged = onChanged;
    }

    public HotbarComponent(HotbarSetting hotbar) {
    	this(hotbar.displayName, hotbar.getValue());
        this.hotbar = hotbar;
        this.hotbar.addOnUpdate(this::onSettingValueChanged);
    }

    private void toggleSlot(int index) {
        values.set(index, !values.get(index));
        updateSlotColors();
        if (hotbar != null)
            hotbar.setValue(new ArrayList<>(values));
        if (onChanged != null)
            onChanged.accept(values);
    }

    private void onSettingValueChanged(List<Boolean> v) {
        if (!v.equals(this.values)) {
            this.values = new ArrayList<>(v);
            updateSlotColors();
        }
    }

    private void updateSlotColors() {
        for (int i = 0; i < slotComponents.length; i++) {
            slotComponents[i].setBackgroundColor(values.get(i) ? COLOR_ON : COLOR_OFF);
        }
    }
}
