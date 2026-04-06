package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.StartAttackEvent;
import net.aoba.event.listeners.StartAttackListener;
import net.aoba.mixin.interfaces.ILocalPlayer;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.minecraft.client.Minecraft;

public class KnockbackDisplacer extends Module implements StartAttackListener {
	public KnockbackDisplacer() {
		super("KBDisplacer");
		setCategory(Category.of("Combat"));
	}

	@Override
	public void onStartAttack(StartAttackEvent event) {
		if(MC.player == null || MC.level == null) return;
		
		if(!MC.player.isSprinting()) return; // TODO: Player can still displace KB if they are holding a KB weapon
		
    	MC.player.setYRot(MC.player.getYRot() + 180);
    	((ILocalPlayer)Minecraft.getInstance().player).invokeSendPosition();
    	MC.player.setYRot(MC.player.getYRot() + 180);
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
}