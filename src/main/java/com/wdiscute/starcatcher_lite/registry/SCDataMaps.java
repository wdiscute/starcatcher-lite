package com.wdiscute.starcatcher_lite.registry;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.fish.Difficulty;
import com.wdiscute.starcatcher_lite.fish.Textures;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public interface SCDataMaps
{
    DataMapType<Item, Difficulty> FISH_DIFFICULTY = DataMapType.builder(
                    StarcatcherLite.rl("fish_difficulty"), Registries.ITEM, Difficulty.CODEC)
            .synced(Difficulty.CODEC, true).build();

    DataMapType<Item, Textures> MINIGAME_TEXTURES = DataMapType.builder(
                    StarcatcherLite.rl("minigame_textures"), Registries.ITEM, Textures.CODEC)
            .synced(Textures.CODEC, true).build();

    static <T> T getOrDefault(Item item, DataMapType<Item, T> dataMap, T d)
    {
        return getOrDefault(item.getDefaultInstance(), dataMap, d);
    }

    static <T> T getOrDefault(ItemStack stack, DataMapType<Item, T> dataMap, T d)
    {
        T data = stack.getItemHolder().getData(dataMap);
        if (data == null) return d;
        return data;
    }

}
