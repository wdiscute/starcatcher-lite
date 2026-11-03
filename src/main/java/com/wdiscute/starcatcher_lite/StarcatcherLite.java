package com.wdiscute.starcatcher_lite;

import com.mojang.logging.LogUtils;
import com.wdiscute.starcatcher_lite.registry.SCSweetSpotsBehaviour;
import com.wdiscute.starcatcher_lite.registry.sweetspotbehaviour.AbstractSweetSpotBehaviour;
import com.wdiscute.starcatcher_lite.registry.*;
import com.wdiscute.utils.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.slf4j.Logger;

import java.text.DecimalFormat;
import java.util.function.Supplier;

@Mod(StarcatcherLite.MOD_ID)
public class StarcatcherLite
{
    public static final String MOD_ID = "starcatcher_lite";

    //resource keys
    public static final ResourceKey<Registry<Supplier<? extends AbstractSweetSpotBehaviour>>> SWEETSPOT_BEHAVIOUR =
            ResourceKey.createRegistryKey(StarcatcherLite.rl("sweetspot_behaviour"));

    //registry
    public static final Registry<Supplier<? extends AbstractSweetSpotBehaviour>> SWEETSPOT_BEHAVIOUR_REGISTRY = new RegistryBuilder<>(SWEETSPOT_BEHAVIOUR)
            .sync(true)
            .defaultKey(StarcatcherLite.rl("normal"))
            .create();

    public static ResourceLocation rl(String s)
    {
        return Utils.rl(StarcatcherLite.MOD_ID, s);
    }

    public StarcatcherLite(IEventBus modEventBus, ModContainer modContainer)
    {
        SCDataAttachments.register(modEventBus);
        SCSweetSpotsBehaviour.register(modEventBus);
        SCAttributes.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, SCConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, SCConfig.SPEC_SERVER);
    }

    @Mod(value = StarcatcherLite.MOD_ID, dist = Dist.CLIENT)
    public static class Client
    {
        public Client(ModContainer modContainer)
        {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}
