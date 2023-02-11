package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.gui.Color;
import net.aoba.misc.RainbowColor;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.network.Packet;

public class EntityESP extends Module {

	private Color color;
	private RainbowColor rainbowColor;

	
	public SliderSetting hue = new SliderSetting("Hue", "chestesp_hue", 4, 0, 360, 1);
	public BooleanSetting rainbow = new BooleanSetting("Rainbow", "chestesp_rainbow");
	public SliderSetting effectSpeed = new SliderSetting("Effect Spd", "chestesp_effectspeed", 4, 1, 20, 0.1);
	
	public EntityESP() {
		this.setName("EntityESP");
		this.setBind(new KeyBinding("key.entityesp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see entities with an ESP.");
		color = new Color(255, 0, 0);
		rainbowColor = new RainbowColor();
		
		this.addSetting(hue);
		this.addSetting(rainbow);
		this.addSetting(effectSpeed);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		if(this.rainbow.getValue()) {
			this.rainbowColor.update(this.effectSpeed.getValueFloat());
		}else {
			this.color.setHSV(hue.getValueFloat(), 1f, 1f);
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {		
		for (Entity entity : MC.world.getEntities()) {
			if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
				if (entity instanceof AnimalEntity) {
					this.getRenderUtils().draw3DBox(matrixStack, entity.getBoundingBox(), new Color(0, 255, 0), 0.2f);
				} else if (entity instanceof Monster) {
					this.getRenderUtils().draw3DBox(matrixStack, entity.getBoundingBox(), new Color(255, 0, 0), 0.2f);
				} else {
					this.getRenderUtils().draw3DBox(matrixStack, entity.getBoundingBox(), new Color(0, 0, 255), 0.2f);
				}
			}
		}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}
