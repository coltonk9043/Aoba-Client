/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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