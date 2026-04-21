/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.Aoba;
import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class HudComponent extends Component {
	private final HudWindow hud;
	private final StringComponent statusComponent;

	public HudComponent(String text, HudWindow hud) {
		this.hud = hud;
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		StringComponent nameComponent = new StringComponent(text);
		nameComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(nameComponent);

		statusComponent = new StringComponent(hud.activated.getValue() ? "-" : "+");
		statusComponent.setProperty(ForegroundProperty, Shader.solid(hud.activated.getValue() ? new Color(255, 0, 0) : new Color(0, 255, 0)));
		statusComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(statusComponent);

		setContent(grid);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				boolean visibility = hud.activated.getValue();
				Aoba.getInstance().guiManager.setHudActive(hud, !visibility);
				e.cancel();
			}
		});
	}

	@Override
	public void update() {
		super.update();

		if (hud.activated.getValue()) {
			statusComponent.setProperty(StringComponent.TextProperty, "-");
			statusComponent.setProperty(ForegroundProperty, Shader.solid(Colors.Red));
		} else {
			statusComponent.setProperty(StringComponent.TextProperty, "+");
			statusComponent.setProperty(ForegroundProperty, Shader.solid(Colors.Green));
		}
	}
}
