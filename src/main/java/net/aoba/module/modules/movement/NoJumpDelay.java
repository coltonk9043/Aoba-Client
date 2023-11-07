package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.mixin.interfaces.ILivingEntity;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;

public class NoJumpDelay extends Module implements TickListener {

	private FloatSetting delay;
	
	public NoJumpDelay() {
		super(new KeybindSetting("key.nojumpdelay", "NoJumpDelay Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("NoJumpDelay");
		this.setCategory(Category.Movement);
		this.setDescription("Makes it so the user can jump very quickly.");
		
		delay = new FloatSetting("nojumpdelay_delay", "Delay", "NoJumpDelay Delay", 1f, 0.0f, 20.0f, 1f);
		this.addSetting(delay);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void OnUpdate(TickEvent event) {
		ILivingEntity ent = (ILivingEntity)MC.player;
		if(ent.getJumpCooldown() > delay.getValue()) {
			ent.setJumpCooldown(delay.getValue().intValue());
		}
	}
}