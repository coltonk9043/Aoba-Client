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
import net.aoba.event.listeners.Render2DListener;
import net.aoba.rendering.Renderer2D;

public class Render2DEvent extends AbstractEvent {
	private final Renderer2D renderer;
	
	public Renderer2D getRenderer() {
		return renderer;
	}

	public Render2DEvent(Renderer2D renderer) {
		this.renderer = renderer;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			Render2DListener renderListener = (Render2DListener) listener;
			renderListener.onRender(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Render2DListener> GetListenerClassType() {
		return Render2DListener.class;
	}
}
