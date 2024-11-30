package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.item.Items;

public class EXPThrower extends Module implements TickListener {
	private FloatSetting pitchSetting = FloatSetting.builder().id("expthrower_pitch").displayName("Pitch")
			.description("The pitch angle for throwing XP bottles.").defaultValue(90.0f).minValue(0f).maxValue(90f)
			.step(1f).build();

	private BooleanSetting autoSwapSetting = BooleanSetting.builder().id("expthrower_auto_swap")
			.displayName("Auto Swap").description("Automatically swap to XP bottles if not in hand.").defaultValue(true)
			.build();

	private FloatSetting throwDelaySetting = FloatSetting.builder().id("expthrower_throw_delay")
			.displayName("Throw Delay").description("Delay between throws in ticks.").defaultValue(20f).minValue(1f)
			.maxValue(100f).step(1f).build();

	private BooleanSetting autoToggleSetting = BooleanSetting.builder().id("expthrower_auto_toggle")
			.displayName("Auto Toggle").description("Automatically toggle off when no XP bottles are found.")
			.defaultValue(true).build();

	private long lastThrowTime = 0;

	public EXPThrower() {
		super("EXPThrower");

		this.setCategory(Category.of("misc"));
		this.setDescription("Automatically uses XP bottles.");

		this.addSetting(pitchSetting);
		this.addSetting(autoSwapSetting);
		this.addSetting(throwDelaySetting);
		this.addSetting(autoToggleSetting);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);

		lastThrowTime = 0;
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastThrowTime < throwDelaySetting.getValue() * 50) {
			return;
		}

		FindItemResult exp = findInHotbar(Items.EXPERIENCE_BOTTLE);
		if (!exp.found()) {
			if (autoToggleSetting.getValue()) {
				toggle();
			}
			return;
		}

		rotatePitch(pitchSetting.getValue());

		if (exp.getHand() != null) {
			MC.interactionManager.interactItem(MC.player, exp.getHand());
		} else if (autoSwapSetting.getValue()) {
			swap(exp.slot(), true);
			MC.interactionManager.interactItem(MC.player, exp.getHand());
			swapBack();
		}

		lastThrowTime = currentTime;
	}
}