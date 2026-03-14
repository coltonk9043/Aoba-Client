/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.function.Consumer;

import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.GuiManager;
import net.aoba.gui.VerticalAlignment;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class CheckboxComponent extends Component {
	private static final Color COLOR_ON = new Color(0, 154, 0, 200);
	private static final Color COLOR_OFF = new Color(154, 0, 0, 200);

	private boolean checked;
	private BooleanSetting setting;
	private Consumer<Boolean> onChanged;
	private Runnable onClick;
	private final RectangleComponent checkBox;

	private CheckboxComponent(String text) {
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		StringComponent label = new StringComponent(text);
		label.setVerticalAlignment(VerticalAlignment.Center);
		grid.addChild(label);

		checkBox = new RectangleComponent(
				checked ? COLOR_ON : COLOR_OFF,
				GuiManager.borderColor.getValue(),
				3f);
		checkBox.setWidth(20f);
		checkBox.setHeight(20f);
		checkBox.setVerticalAlignment(VerticalAlignment.Center);
		grid.addChild(checkBox);

		addChild(grid);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				toggle();
				e.cancel();
			}
		});
	}
	
	public CheckboxComponent(String text, boolean checked, Consumer<Boolean> onChanged) {
		this(text);
		this.checked = checked;
		this.onChanged = onChanged;
		checkBox.setBackgroundColor(checked ? COLOR_ON : COLOR_OFF);
	}

	public CheckboxComponent(BooleanSetting setting) {
		this(setting.displayName);
		this.checked = setting.getValue();
		this.setting = setting;
		this.setting.addOnUpdate(this::onSettingValueChanged);
		checkBox.setBackgroundColor(checked ? COLOR_ON : COLOR_OFF);
	}

	private void toggle() {
		checked = !checked;
		checkBox.setBackgroundColor(checked ? COLOR_ON : COLOR_OFF);
		if (setting != null)
			setting.setValue(checked);
		if (onChanged != null)
			onChanged.accept(checked);
		if (onClick != null)
			onClick.run();
	}

	private void onSettingValueChanged(Boolean v) {
		if (v != this.checked) {
			this.checked = v;
			checkBox.setBackgroundColor(checked ? COLOR_ON : COLOR_OFF);
		}
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		checkBox.setBackgroundColor(checked ? COLOR_ON : COLOR_OFF);
		if (setting != null)
			setting.setValue(checked);
	}

	public boolean isChecked() {
		return checked;
	}
}
