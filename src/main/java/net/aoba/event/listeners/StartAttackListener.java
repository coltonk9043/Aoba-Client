package net.aoba.event.listeners;

import net.aoba.event.events.StartAttackEvent;

public interface StartAttackListener extends AbstractListener {
	void onStartAttack(StartAttackEvent event);
}
