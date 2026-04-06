package net.aoba.event.events;

import java.util.ArrayList;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.StartAttackListener;
import net.minecraft.world.entity.Entity;

public class StartAttackEvent extends AbstractEvent {
	private final Entity targetEntity;
	
	public StartAttackEvent(Entity target) {
		targetEntity = target;
	}
	
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			StartAttackListener startAttackListener = (StartAttackListener) listener;
			startAttackListener.onStartAttack(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<StartAttackListener> GetListenerClassType() {
		return StartAttackListener.class;
	}
	
	public Entity getTarget() {
		return targetEntity;
	}
}