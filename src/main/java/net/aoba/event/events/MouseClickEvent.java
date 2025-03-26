/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseClickListener;

public class MouseClickEvent extends AbstractEvent {

	public final double mouseX;
	public final double mouseY;
	public final int button;
	public final int action;
	public final int buttonNumber;
	public final int mods;

	public MouseClickEvent(double mouseX, double mouseY, int button, int action, int mods) {
        this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.button = button;
		this.action = action;
		buttonNumber = -1;
		this.mods = mods;
	}

	public MouseClickEvent(double mouseX, double mouseY, int button, int action, int mods, int buttonNumber) {
        this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.button = button;
		this.action = action;
		this.mods = mods;
		this.buttonNumber = buttonNumber;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			MouseClickListener mouseClickListener = (MouseClickListener) listener;
			mouseClickListener.onMouseClick(this);

			if (isCancelled)
				break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<MouseClickListener> GetListenerClassType() {
		return MouseClickListener.class;
	}
}