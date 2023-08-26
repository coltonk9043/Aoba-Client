package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.RenderListener;
import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent extends AbstractEvent {
	MatrixStack matrixStack; 
	float partialTicks;
	
	public MatrixStack GetMatrixStack() {
		return matrixStack;
	}
	public float GetPartialTicks() {
		return partialTicks;
	}
	
	public RenderEvent(MatrixStack matrixStack, float partialTicks) {
		this.matrixStack = matrixStack;
		this.partialTicks = partialTicks;
	}
	
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : listeners) {
			RenderListener renderListener = (RenderListener) listener;
			renderListener.OnRender(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<RenderListener> GetListenerClassType() {
		return RenderListener.class;
	}
}
