package net.aoba.module.modules.render;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;
import net.aoba.gui.Color;
import net.aoba.misc.ModuleUtils;
import net.aoba.misc.RainbowColor;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;

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
		color = new Color(hue.getValueFloat(), 1f, 1f);
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
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		ArrayList<BlockEntity> blockEntities = ModuleUtils.getTileEntities().collect(Collectors.toCollection(ArrayList::new));
		
		for(BlockEntity blockEntity : blockEntities) {
			if(blockEntity instanceof ChestBlockEntity || blockEntity instanceof TrappedChestBlockEntity) {
				Box box = new Box(blockEntity.getPos());
				this.getRenderUtils().draw3DBox(matrixStack, box, currentColor, 0.2f);
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
