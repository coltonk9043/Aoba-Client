/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils;

import net.aoba.AobaClient;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.Objects;
import java.util.stream.Stream;

public class ModuleUtils {

    public static boolean isThrowable(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.BOW || item == Items.SNOWBALL || item == Items.EGG || item == Items.FIRE_CHARGE || item == Items.TRIDENT || item instanceof EnderpearlItem || item instanceof SplashPotionItem || item instanceof LingeringPotionItem || item instanceof FishingRodItem || item instanceof EnderEyeItem;
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

    public static Stream<LevelChunk> getLoadedChunks() {
        int radius = Math.max(2, AobaClient.MC.options.getEffectiveRenderDistance()) + 3;
        int diameter = radius * 2 + 1;

        ChunkPos center = AobaClient.MC.player.chunkPosition();
        ChunkPos min = new ChunkPos(center.x() - radius, center.z() - radius);
        ChunkPos max = new ChunkPos(center.x() + radius, center.z() + radius);

        Stream<LevelChunk> stream = Stream.iterate(min, pos -> {
            int x = pos.x();
            int z = pos.z();
            x++;

            if (x > max.x()) {
                x = min.x();
                z++;
            }

            return new ChunkPos(x, z);

        }).limit((long) diameter * diameter).filter(c -> AobaClient.MC.level.hasChunk(c.x(), c.z())).map(c -> AobaClient.MC.level.getChunk(c.x(), c.z())).filter(Objects::nonNull);

        return stream;
    }
}
