package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;

import net.aoba.gui.Color;
import net.aoba.misc.RainbowColor;
import net.aoba.module.Module;
import net.aoba.settings.ListSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;

import net.minecraft.network.Packet;

public class EntityESP extends Module {

	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	
	public ListSetting colorEffect = new ListSetting("Color Effect", "entityesp_coloreffect", new String[]{"Fixed Color", "Rainbow"});
	public SliderSetting hue = new SliderSetting("Hue", "chestesp_hue", 4, 0, 360, 1);
	public SliderSetting effectSpeed = new SliderSetting("Effect Spd", "chestesp_effectspeed", 4, 1, 20, 0.1);
	
	public EntityESP() {
		this.setName("EntityESP");
		this.setBind(new KeyBinding("key.entityesp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see entities with an ESP.");
		color = new Color(255, 0, 0);
		currentColor = color;
		rainbowColor = new RainbowColor();
		this.addSetting(colorEffect);
		
		this.addSetting(hue);
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
		if(this.colorEffect.getValue().equalsIgnoreCase("Fixed Color")) {
			this.color.setHSV(hue.getValueFloat(), 1f, 1f);
			this.currentColor = color;
		}else if (this.colorEffect.getValue().equalsIgnoreCase("Rainbow")) {
			this.rainbowColor.update(this.effectSpeed.getValueFloat());
			this.currentColor = this.rainbowColor.getColor();
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack) {
		for (Entity entity : mc.world.getEntities()) {
			if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
				//if (entity instanceof MobEntity) {
				//	this.getRenderUtils().EntityESPBox((Entity) entity, 0, 1f, 0);
				//} else if (entity instanceof Monster) {
				//	this.getRenderUtils().EntityESPBox((Entity) entity, 1f, 0, 0);
				//} else {
					this.getRenderUtils().draw3DBox(matrixStack, entity.getBoundingBox(), new Color(1f, 0f, 0f));
				//}
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
