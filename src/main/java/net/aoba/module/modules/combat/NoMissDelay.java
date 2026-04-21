package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.StartAttackEvent;
import net.aoba.event.listeners.StartAttackListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.world.phys.HitResult;

public class NoMissDelay extends Module implements StartAttackListener {
	public NoMissDelay() {
		super("NoMissDelay");
		
		setCategory(Category.of("Combat"));
		setDescription("Prevents you from swinging while looking at air");
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(StartAttackListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(StartAttackListener.class, this);
	}

	@Override
	public void onToggle() {
		
	}

	@Override
	public void onStartAttack(StartAttackEvent event) {
		if(MC.hitResult != null && MC.hitResult.getType() == HitResult.Type.MISS) {
			event.cancel();
		}
	}
}
