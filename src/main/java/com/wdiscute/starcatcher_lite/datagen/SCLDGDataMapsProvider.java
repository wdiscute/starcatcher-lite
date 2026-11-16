package com.wdiscute.starcatcher_lite.datagen;

import com.wdiscute.starcatcher_lite.fish.Difficulty;
import com.wdiscute.starcatcher_lite.fish.Textures;
import com.wdiscute.starcatcher_lite.registry.SCLDataMaps;
import com.wdiscute.utils.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class SCLDGDataMapsProvider extends DataMapProvider
{
    protected SCLDGDataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider)
    {
        //difficulty
        var difficulty = this.builder(SCLDataMaps.FISH_DIFFICULTY);
        difficulty.add(Items.COD.builtInRegistryHolder(), Difficulty.EASY, false);
        difficulty.add(Items.LEATHER.builtInRegistryHolder(), Difficulty.TRASH, false);

        //textures
        Builder<Textures, Item> textures = this.builder(SCLDataMaps.MINIGAME_TEXTURES);

        addNether(textures, Utils.rl("blaze_powder"));

        //nether depths upgrade
        addNether(textures, Utils.rl("netherdepthsupgrade", "lava_pufferfish"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "obsidianfish"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "searing_cod"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "bonefish"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "wither_bonefish"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "blazefish"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "magmacubefish"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "glowdine"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "soulsucker"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "fortress_grouper"));
        addNether(textures, Utils.rl("netherdepthsupgrade", "eyeball_fish"));
    }

    public void addNether(Builder<Textures, Item> builder, ResourceLocation id)
    {
        if (id.getNamespace().equals("minecraft"))
            builder.add(id, Textures.DEFAULT.withTank(Textures.NETHER), false);
        else
            builder.add(id, Textures.DEFAULT.withTank(Textures.NETHER), false, new ModLoadedCondition(id.getNamespace()));
    }
}
