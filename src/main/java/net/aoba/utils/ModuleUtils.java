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
 * A helper class that contains several useful functions.
 */
package net.aoba.utils;

import net.aoba.AobaClient;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Objects;
import java.util.stream.Stream;

public class ModuleUtils {

    public static boolean isThrowable(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.BOW || item == Items.SNOWBALL || item == Items.EGG || item == Items.FIRE_CHARGE || item == Items.TRIDENT || item instanceof EnderPearlItem || item instanceof SplashPotionItem || item instanceof LingeringPotionItem || item instanceof FishingRodItem || item instanceof EnderEyeItem;
    }

    public static boolean isPlantable(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.WHEAT_SEEDS || item == Items.CARROT || item == Items.POTATO
                || item == Items.BEETROOT_SEEDS || item == Items.MELON_SEEDS || item == Items.COCOA_BEANS
                || item == Items.NETHER_WART;
    }

    public static Stream<BlockEntity> getTileEntities() {
        return getLoadedChunks().flatMap(chunk -> chunk.getBlockEntities().values().stream());
    }

    public static Stream<WorldChunk> getLoadedChunks() {
        int radius = Math.max(2, AobaClient.MC.options.getClampedViewDistance()) + 3;
        int diameter = radius * 2 + 1;

        ChunkPos center = AobaClient.MC.player.getChunkPos();
        ChunkPos min = new ChunkPos(center.x - radius, center.z - radius);
        ChunkPos max = new ChunkPos(center.x + radius, center.z + radius);

        Stream<WorldChunk> stream = Stream.<ChunkPos>iterate(min, pos -> {
            int x = pos.x;
            int z = pos.z;
            x++;

            if (x > max.x) {
                x = min.x;
                z++;
            }

            return new ChunkPos(x, z);

        }).limit((long) diameter * diameter).filter(c -> AobaClient.MC.world.isChunkLoaded(c.x, c.z)).map(c -> AobaClient.MC.world.getChunk(c.x, c.z)).filter(Objects::nonNull);

        return stream;
    }
}
