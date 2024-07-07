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

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.Render3DListener;
import net.minecraft.client.util.math.MatrixStack;

<<<<<<< Updated upstream:src/main/java/net/aoba/event/events/RenderEvent.java
import java.util.ArrayList;
import java.util.List;

public class RenderEvent extends AbstractEvent {
    MatrixStack matrices;
    float partialTicks;

    public MatrixStack GetMatrix() {
        return matrices;
    }

    public float GetPartialTicks() {
        return partialTicks;
    }

    public RenderEvent(MatrixStack matrix4f, float partialTicks) {
        this.matrices = matrix4f;
        this.partialTicks = partialTicks;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            RenderListener renderListener = (RenderListener) listener;
            renderListener.OnRender(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<RenderListener> GetListenerClassType() {
        return RenderListener.class;
    }
=======
public class Render3DEvent extends AbstractEvent {
	MatrixStack matrices; 
	float partialTicks;
	
	public MatrixStack GetMatrix() {
		return matrices;
	}
	public float GetPartialTicks() {
		return partialTicks;
	}
	
	public Render3DEvent(MatrixStack matrix4f, float partialTicks) {
		this.matrices = matrix4f;
		this.partialTicks = partialTicks;
	}
	
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : List.copyOf(listeners)) {
			Render3DListener renderListener = (Render3DListener) listener;
			renderListener.OnRender(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Render3DListener> GetListenerClassType() {
		return Render3DListener.class;
	}
>>>>>>> Stashed changes:src/main/java/net/aoba/event/events/Render3DEvent.java
}
