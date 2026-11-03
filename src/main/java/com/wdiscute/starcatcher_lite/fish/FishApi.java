package com.wdiscute.starcatcher_lite.fish;

import com.wdiscute.starcatcher_lite.registry.SCDataAttachments;
import com.wdiscute.starcatcher_lite.registry.SCDataMaps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

public class FishApi
{
    public static Difficulty getDifficultyForItem(Item item)
    {
        return SCDataMaps.getOrDefault(item, SCDataMaps.FISH_DIFFICULTY, Difficulty.EASY);
    }

    public static Textures getTexturesForItem(Item item)
    {
        return SCDataMaps.getOrDefault(item, SCDataMaps.MINIGAME_TEXTURES, Textures.DEFAULT);
    }

    public static void playerCompletedMinigame(ServerPlayer player, boolean completed)
    {
        if(player.fishing == null)
        {
            player.removeData(SCDataAttachments.ITEMS_BEING_FISHED);
            player.removeData(SCDataAttachments.IN_FISHING_MINIGAME);
            return;
        }

        if(completed)
        {
            player.fishing.retrieve(player.getMainHandItem().is(Tags.Items.RODS) ? player.getMainHandItem() : player.getOffhandItem());
        }
    }
}
