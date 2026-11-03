package com.wdiscute.starcatcher_lite.datagen;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = StarcatcherLite.MOD_ID)
public class DGSCDataGenerators
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        PackOutput output = gen.getPackOutput();

        //data maps
        gen.addProvider(event.includeServer(), new DGSCDataMapsProvider(output, event.getLookupProvider()));
    }
}
