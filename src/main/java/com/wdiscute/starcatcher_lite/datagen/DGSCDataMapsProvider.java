package com.wdiscute.starcatcher_lite.datagen;

import com.wdiscute.starcatcher_lite.fish.Difficulty;
import com.wdiscute.starcatcher_lite.fish.Textures;
import com.wdiscute.starcatcher_lite.registry.SCDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class DGSCDataMapsProvider extends DataMapProvider
{
    protected DGSCDataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider)
    {
        //difficulty
        var difficulty = this.builder(SCDataMaps.FISH_DIFFICULTY);
        difficulty.add(Items.COD.builtInRegistryHolder(), Difficulty.EASY, false);
        difficulty.add(Items.LEATHER.builtInRegistryHolder(), Difficulty.TRASH, false);

        //textures
        var textures = this.builder(SCDataMaps.MINIGAME_TEXTURES);
        textures.add(Items.COD.builtInRegistryHolder(), Textures.DEFAULT.withTank(Textures.NETHER), false);
    }
}
