package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.interfaces.IHorseBaseEntity;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;

public class EntityControl extends Module implements TickListener {
	public EntityControl() {
		super("EntityControl");
		this.setDescription("Allows you to control entities without needing a saddle.");
		this.setCategory(Category.of("Movement"));
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);

		for (Entity entity : MC.world.getEntities()) {
			if (entity instanceof AbstractHorseEntity)
				((IHorseBaseEntity) entity).setSaddled(false);
		}
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		for (Entity entity : MC.world.getEntities()) {
			if (entity instanceof AbstractHorseEntity)
				((IHorseBaseEntity) entity).setSaddled(true);
		}
	}
}
