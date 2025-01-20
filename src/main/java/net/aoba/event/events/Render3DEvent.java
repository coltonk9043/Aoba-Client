/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.Render3DListener;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent extends AbstractEvent {
	MatrixStack matrices;
	Frustum frustum;
	RenderTickCounter renderTickCounter;

	public MatrixStack GetMatrix() {
		return matrices;
	}

	public RenderTickCounter getRenderTickCounter() {
		return renderTickCounter;
	}

	public Frustum getFrustum() {
		return frustum;
	}

	public Render3DEvent(MatrixStack matrix4f, Frustum frustum, RenderTickCounter renderTickCounter) {
		this.matrices = matrix4f;
		this.renderTickCounter = renderTickCounter;
		this.frustum = frustum;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : List.copyOf(listeners)) {
			Render3DListener renderListener = (Render3DListener) listener;
			renderListener.onRender(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Render3DListener> GetListenerClassType() {
		return Render3DListener.class;
	}
}