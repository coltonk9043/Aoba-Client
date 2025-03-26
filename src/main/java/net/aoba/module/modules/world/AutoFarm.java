/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.world;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.InteractionManager;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AutoFarm extends Module implements TickListener {

	private final FloatSetting radius = FloatSetting.builder().id("autofarm_radius").displayName("Radius")
			.description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

	public AutoFarm() {
		super("AutoFarm");
		setCategory(Category.of("World"));
		setDescription("Automatically plants, fertilizes, and harvests crops.");
		addSetting(radius);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu
		);
	}

	public void setRadius(int radius) {
		this.radius.setValue((float) radius);
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
		int rad = radius.getValue().intValue();
		BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();

		for (int x = -rad; x < rad; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -rad; z < rad; z++) {
					mutableBlockPos.set(x, y, z);
					Block block = MC.world.getBlockState(mutableBlockPos).getBlock();
					BlockState blockState = MC.world.getBlockState(mutableBlockPos);

					if (block instanceof CropBlock crop) {
						if (!crop.canGrow(MC.world, null, mutableBlockPos, blockState)) {
							InteractionManager.destroyBlock(mutableBlockPos);
						} else {
							fertilizeCrops(mutableBlockPos);
						}
					} else if (block instanceof FarmlandBlock) {
						handleFarmland(mutableBlockPos);
					}
				}
			}
		}
	}

	private void fertilizeCrops(BlockPos.Mutable mutableBlockPos) {
		if (InteractionManager.selectItem(stack -> stack.getItem() == Items.BONE_MEAL)) {
			InteractionManager.useItemOnBlock(mutableBlockPos, Hand.MAIN_HAND);
		}
	}

	private void handleFarmland(BlockPos.Mutable mutableBlockPos) {
		BlockPos blockAbovePos = mutableBlockPos.up();
		Block blockAbove = MC.world.getBlockState(blockAbovePos).getBlock();
		if (blockAbove == Blocks.AIR) {
			if (InteractionManager.selectItem(ModuleUtils::isPlantable)) {
				InteractionManager.useItemOnBlock(mutableBlockPos, Hand.MAIN_HAND);
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}