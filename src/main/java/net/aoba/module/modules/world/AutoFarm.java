/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * AutoFarm Module
 */
package net.aoba.module.modules.world;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.InteractionManager;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.ModuleUtils;
import net.minecraft.block.*;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AutoFarm extends Module implements TickListener
{

    private FloatSetting radius = FloatSetting.builder().id("autofarm_radius").displayName("Radius").description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

    public AutoFarm()
    {
        super("AutoFarm");
        this.setCategory(Category.of("World"));
        this.setDescription("Automatically plants, fertilizes, and harvests crops.");
        this.addSetting(radius);
    }

    public void setRadius(int radius)
    {
        this.radius.setValue((float) radius);
    }

    @Override
    public void onDisable()
    {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
    }

    @Override
    public void onEnable()
    {
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle()
    {
    }

    @Override
    public void onTick(Pre event)
    {
        int rad = radius.getValue().intValue();
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();

        for (int x = -rad; x < rad; x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                for (int z = -rad; z < rad; z++)
                {
                    mutableBlockPos.set(x, y, z);
                    Block block = MC.world.getBlockState(mutableBlockPos).getBlock();
                    BlockState blockState = MC.world.getBlockState(mutableBlockPos);

                    if (block instanceof CropBlock crop)
                    {
                        if (!crop.canGrow(MC.world, null, mutableBlockPos, blockState))
                        {
                            InteractionManager.destroyBlock(mutableBlockPos);
                        }
                        else
                        {
                            fertilizeCrops(mutableBlockPos);
                        }
                    }
                    else if (block instanceof FarmlandBlock)
                    {
                        handleFarmland(mutableBlockPos);
                    }
                }
            }
        }
    }

    private void fertilizeCrops(BlockPos.Mutable mutableBlockPos)
    {
        if (InteractionManager.selectItem(stack -> stack.getItem() == Items.BONE_MEAL))
        {
            InteractionManager.useItemOnBlock(mutableBlockPos, Hand.MAIN_HAND);
        }
    }

    private void handleFarmland(BlockPos.Mutable mutableBlockPos)
    {
        BlockPos blockAbovePos = mutableBlockPos.up();
        Block blockAbove = MC.world.getBlockState(blockAbovePos).getBlock();
        if (blockAbove == Blocks.AIR)
        {
            if (InteractionManager.selectItem(ModuleUtils::isPlantable))
            {
                InteractionManager.useItemOnBlock(mutableBlockPos, Hand.MAIN_HAND);
            }
        }
    }


    @Override
    public void onTick(Post event)
    {

    }
}