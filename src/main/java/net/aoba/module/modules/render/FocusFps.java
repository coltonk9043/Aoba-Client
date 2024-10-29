package net.aoba.module.modules.render;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class FocusFps extends Module {
	private FloatSetting fps = FloatSetting.builder().id("focusfps_fps").displayName("FPS")
			.description("The FPS for when the window is not in focus.").defaultValue(30f).minValue(1f).maxValue(45f)
			.step(1f).build();

	public FocusFps() {
		super("FocusFPS");
		this.setCategory(Category.of("Render"));
		this.setDescription("Limits the FPS of the game when it is not focused.");
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

	public Float getFps() {
		return this.fps.getValue();
	}
}
