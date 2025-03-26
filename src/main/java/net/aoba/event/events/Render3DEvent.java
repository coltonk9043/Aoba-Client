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
import net.aoba.event.listeners.Render3DListener;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent extends AbstractEvent {
	MatrixStack matrices;
	Frustum frustum;
	RenderTickCounter renderTickCounter;
	Camera camera;

	public MatrixStack GetMatrix() {
		return matrices;
	}

	public RenderTickCounter getRenderTickCounter() {
		return renderTickCounter;
	}

	public Frustum getFrustum() {
		return frustum;
	}

	public Camera getCamera() {
		return camera;
	}

	public Render3DEvent(MatrixStack matrix4f, Frustum frustum, Camera camera, RenderTickCounter renderTickCounter) {
		matrices = matrix4f;
		this.renderTickCounter = renderTickCounter;
		this.frustum = frustum;
		this.camera = camera;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
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