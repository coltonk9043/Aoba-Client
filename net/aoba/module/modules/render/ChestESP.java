package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;

import net.aoba.gui.Color;
import net.aoba.interfaces.IWorld;
import net.aoba.misc.RainbowColor;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

public class ChestESP extends Module {
	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	public SliderSetting hue = new SliderSetting("Hue", "chestesp_hue", 4, 0, 360, 1);
	public BooleanSetting rainbow = new BooleanSetting("Rainbow", "chestesp_rainbow");
	public SliderSetting effectSpeed = new SliderSetting("Effect Spd", "chestesp_effectspeed", 4, 1, 20, 0.1);
	
	public ChestESP() {
		this.setName("ChestESP");
		this.setBind(new KeyBinding("key.chestesp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see Chests with an ESP.");
		color = new Color(hue.getValueFloat());
		currentColor = color;
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
			this.currentColor = this.rainbowColor.getColor();
		}else {
			this.color.setHSV(hue.getValueFloat(), 1f, 1f);
			this.currentColor = color;
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack) {

		for (BlockEntityTickInvoker tickInvolker : ((IWorld)mc.world).getBlockEntityTickers()) {
			BlockEntity blockEntity = mc.world.getBlockEntity(tickInvolker.getPos());
			
			System.out.println(blockEntity.getPos().getX()+ ", " + blockEntity.getPos().getY() + ", " + blockEntity.getPos().getZ());
			if(blockEntity instanceof ChestBlockEntity) {
				Box box = new Box(blockEntity.getPos());
				
				this.getRenderUtils().draw3DBox(matrixStack, box, currentColor);
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
