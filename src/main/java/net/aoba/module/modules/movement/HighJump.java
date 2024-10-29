package net.aoba.module.modules.movement;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class HighJump extends Module {

	private FloatSetting multiplier = FloatSetting.builder().id("highjump_jumpmultiplier")
			.displayName("Jump Multiplier").description("The height that the player will jump.").defaultValue(1.5f)
			.minValue(0.1f).maxValue(10f).step(0.1f).build();

	public HighJump() {
		super("HighJump");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Allows the player to jump super high!");

		this.addSetting(multiplier);
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

	public float getJumpHeightMultiplier() {
		return multiplier.getValue();
	}
}