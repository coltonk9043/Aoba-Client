/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.GuiManager;
import net.aoba.gui.HorizontalAlignment;
import net.aoba.gui.TextWrapping;
import net.aoba.gui.Thickness;
import net.aoba.gui.VerticalAlignment;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class KeybindComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;
	private Key key;
	private KeybindSetting keyBind;
	private Consumer<Key> onChanged;
	private final StringComponent label;
	private final StringComponent keyTextComponent;

	public KeybindComponent() {
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		label = new StringComponent(getDisplayText());
		label.setVerticalAlignment(VerticalAlignment.Center);
		grid.addChild(label);

		RectangleComponent keyButton = new RectangleComponent(
				new Color(115, 115, 115, 200),
				GuiManager.borderColor.getValue(),
				3f);
		keyButton.setPadding(new Thickness(10f, 4f, 10f, 4f));
		keyButton.setVerticalAlignment(VerticalAlignment.Center);

		keyTextComponent = new StringComponent("N/A");
		keyTextComponent.setVerticalAlignment(VerticalAlignment.Center);
		keyTextComponent.setHorizontalAlignment(HorizontalAlignment.Center);
		keyTextComponent.setTextWrapping(TextWrapping.NoWrap);
		keyTextComponent.setIsHitTestVisible(false);
		keyButton.addChild(keyTextComponent);

		keyButton.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				setListeningForKey(true);
				e.cancel();
			}
		});

		grid.addChild(keyButton);
		addChild(grid);
	}
	
	public KeybindComponent(KeybindSetting keyBind) {
		this();
		this.key = keyBind.getValue();
		this.keyBind = keyBind;
		this.keyBind.addOnUpdate(this::onSettingValueChanged);
		label.setText(getDisplayText());
		keyTextComponent.setText(getKeyDisplayText());
	}

	public KeybindComponent(Key key, Consumer<Key> onChanged) {
		this();
		this.key = key;
		this.onChanged = onChanged;
		keyTextComponent.setText(getKeyDisplayText());
	}

	private String getDisplayText() {
		if(this.keyBind == null)
			return "Keybind";
		else 
			return keyBind.displayName;
	}
	
	private String getKeyDisplayText() {
		String text = getKey().getDisplayName().getString();
		if (text.equals("scancode.0") || text.equals("key.keyboard.0"))
			return "N/A";
		return text;
	}

	private void onSettingValueChanged(Key newKey) {
		if (newKey != this.key) {
			this.key = newKey;
			keyTextComponent.setText(getKeyDisplayText());
		}
	}

	public Key getKey() {
		return key;
	}

	public void setKeyBind(Key key) {
		this.key = key;
		if (keyBind != null)
			keyBind.setValue(key);
		keyTextComponent.setText(getKeyDisplayText());
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (isVisible()) {
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			this.key = event.GetKey() == GLFW.GLFW_KEY_ESCAPE
					? InputConstants.UNKNOWN
					: InputConstants.Type.KEYSYM.getOrCreate(event.GetKey());

			if (keyBind != null)
				keyBind.setValue(this.key);
			if (onChanged != null)
				onChanged.accept(this.key);

			keyTextComponent.setText(getKeyDisplayText());
			listeningForKey = false;
			event.cancel();
		}
	}

	private void setListeningForKey(boolean state) {
		listeningForKey = state;
		GuiManager.setKeyboardInputActive(state);
		if (listeningForKey) {
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}
}
