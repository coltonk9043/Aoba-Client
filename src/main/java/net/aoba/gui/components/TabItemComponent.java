/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.UIProperty;

public class TabItemComponent extends Component {
	public static final UIProperty<String> HeaderProperty = new UIProperty<>(
			"Header", "", false, true);

	public TabItemComponent() {
	}

	public TabItemComponent(String header) {
		setProperty(HeaderProperty, header);
	}
}
