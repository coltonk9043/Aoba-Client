package net.aoba.module.modules.combat;

import java.util.Random;

import net.aoba.Aoba;
import net.aoba.event.events.StartAttackEvent;
import net.aoba.event.listeners.StartAttackListener;
import net.aoba.mixin.interfaces.ILocalPlayer;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.Minecraft;

public class KnockbackDisplacer extends Module implements StartAttackListener {
	private static final Random rnd = new Random();
	
	private final FloatSetting angle = FloatSetting.builder().id("displacement_angle").displayName("Angle")
			.description("The angle by which the knockback is displaced").defaultValue(180.0f).minValue(-180.0f)
			.maxValue(180.0f).step(1.0f).build();

	private final FloatSetting random = FloatSetting.builder().id("displacement_angle_random").displayName("Angle randomization")
			.description("Displacement angle randomization").defaultValue(0.0f).minValue(0.0f)
			.maxValue(180.0f).step(1.0f).build();
	
	public KnockbackDisplacer() {
		super("KBDisplacer");
		setCategory(Category.of("Combat"));
		
		addSettings(angle, random);
	}

	@Override
	public void onStartAttack(StartAttackEvent event) {
		if(MC.player == null || MC.level == null) return;
		
		if(!MC.player.isSprinting()) return; // TODO: Player can still displace KB if they are holding a KB weapon
		
		float displacement = angle.getValue();
		float random = this.random.getValue();
		
		if(random != 0)
			displacement += rnd.nextFloat(-random, +random);
		
    	MC.player.setYRot(MC.player.getYRot() + displacement);
    	((ILocalPlayer)Minecraft.getInstance().player).invokeSendPosition();
    	MC.player.setYRot(MC.player.getYRot() - displacement);
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