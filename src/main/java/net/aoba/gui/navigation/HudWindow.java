/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation;

import net.aoba.Aoba;
import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Color;
import net.aoba.gui.types.Rectangle;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;

public class HudWindow extends Window {
	private static final Shader hoverColor = Shader.solid(new Color(255, 0, 0));
	private static final Shader dragColor = Shader.solid(new Color(255, 0, 0, 165));
	
	public BooleanSetting activated;

	public CloseableWindow optionsWindow;

	public HudWindow(String ID, float x, float y) {
		this(ID, x, y, 180.0f, 50f);
	}

	public HudWindow(String ID, float x, float y, float width, float height) {
		super(ID, x, y, width, height);
		activated = BooleanSetting.builder().id(ID + "_activated").defaultValue(false)
				.onUpdate(this::onActivatedChanged).build();

		SettingManager.registerSetting(activated);
	}

	private void onActivatedChanged(Boolean state) {
		Aoba.getInstance().guiManager.setHudActive(this, state.booleanValue());
	}


	@Override
	protected void onVisibilityChanged(Boolean oldValue, Boolean newValue) {
		if (!newValue) {
			isMoving = false;
			if (activated != null && activated.getValue()) {
				setProperty(UIElement.IsVisibleProperty, true);
			}
		}
	}
	
	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		if (getProperty(UIElement.IsVisibleProperty)) {
			UIElement content = getContent();
			if (content != null && content.getProperty(UIElement.IsVisibleProperty)) {
				content.draw(renderer, partialTicks);
			}

			Rectangle pos = getActualSize();

			float x = pos.x();
			float y = pos.y();
			float width = pos.width();
			float height = pos.height();

			if (isMoving) {
				renderer.drawBox(x, y, width, height, dragColor);
			}
			if (Aoba.getInstance().guiManager.isClickGuiOpen()) {
				renderer.drawBoxOutline(x, y, width, height, hoverColor);
			}
		}
	}
}
