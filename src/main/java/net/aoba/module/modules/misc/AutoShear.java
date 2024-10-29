package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoShear extends Module implements TickListener {

	private FloatSetting radius = FloatSetting.builder().id("autoshear_radius").displayName("Radius")
			.description("Radius that AutoShear will trigger on Mobs.").defaultValue(5f).minValue(0.1f).maxValue(10f)
			.step(0.1f).build();

	public AutoShear() {
		super("AutoShear");

		this.setCategory(Category.of("Misc"));
		this.setDescription("Automatically shears Sheep that are near you.");

		this.addSetting(radius);
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
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		for (Entity entity : MC.world.getEntities()) {
			if (!(entity instanceof SheepEntity))
				continue;

			SheepEntity sheep = (SheepEntity) entity;

			if (!sheep.isShearable() || sheep.isSheared() || sheep.isBaby())
				continue;

			if (MC.player.squaredDistanceTo(entity) > radius.getValueSqr())
				continue;

			FindItemResult shearItemSlot = findInHotbar(Items.SHEARS);
			if (shearItemSlot.found()) {
				swap(shearItemSlot.slot(), false);
				Hand hand = shearItemSlot.getHand();
				MC.interactionManager.interactEntity(MC.player, entity, hand);
			}
		}
	}
}
