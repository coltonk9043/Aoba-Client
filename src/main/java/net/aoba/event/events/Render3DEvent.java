/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.Render3DListener;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;

public class Render3DEvent extends AbstractEvent {
	PoseStack matrices;
	Frustum frustum;
	DeltaTracker renderTickCounter;
	Camera camera;

	public PoseStack GetMatrix() {
		return matrices;
	}

	public DeltaTracker getRenderTickCounter() {
		return renderTickCounter;
	}

	public Frustum getFrustum() {
		return frustum;
	}

	public Camera getCamera() {
		return camera;
	}

	public Render3DEvent(PoseStack matrix4f, Frustum frustum, Camera camera, DeltaTracker renderTickCounter) {
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