package aoba.main.module.modules.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import aoba.main.gui.Color;
import aoba.main.misc.RainbowColor;
import aoba.main.module.Module;
import aoba.main.settings.BooleanSetting;
import aoba.main.settings.SliderSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;

public class Breadcrumbs extends Module{
	private Color currentColor;
	private Color color;
	private RainbowColor rainbowColor;

	public SliderSetting hue = new SliderSetting("Hue", "breadcrumbs_hue", 4, 0, 360, 1);
	public BooleanSetting rainbow = new BooleanSetting("Rainbow", "breadcrumbs_rainbow");
	public SliderSetting effectSpeed = new SliderSetting("Effect Spd", "breadcrumbs_effectspeed", 4, 1, 20, 0.1);
	
	private float timer = 10;
	private float currentTick = 0;
	private List<Vector3d> positions = new ArrayList<Vector3d>();
	
	public Breadcrumbs() {
		this.setName("Breadcrumbs");
		this.setBind(new KeyBinding("key.breadcrumbs", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Shows breadcrumbs of where you last stepped;");
		color = new Color(hue.getValueFloat());
		currentColor = color;
		rainbowColor = new RainbowColor();
		this.addSetting(hue);
		this.addSetting(rainbow);
		this.addSetting(effectSpeed);
	}
	
	@Override
	public void onDisable() {
		this.positions.clear();
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onToggle() {

	}
 
	@Override
	public void onUpdate() {
		currentTick++;
		if(timer == currentTick) {
			currentTick = 0;
			positions.add(mc.player.getPositionVec());
		}
		if(this.rainbow.getValue()) {
			this.rainbowColor.update(this.effectSpeed.getValueFloat());
			this.currentColor = this.rainbowColor.getColor();
		}else {
			this.color.setHSV(hue.getValueFloat(), 1f, 1f);
			this.currentColor = color;
		}
	}

	@Override
	public void onRender() {
		for(int i = 0; i < this.positions.size() - 1; i++) {
			this.getRenderUtils().drawLine3D(this.positions.get(i), this.positions.get(i + 1), this.currentColor);
		}
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		
	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {

	}
}