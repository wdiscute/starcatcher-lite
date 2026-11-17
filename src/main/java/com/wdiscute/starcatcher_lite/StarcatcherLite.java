package com.wdiscute.starcatcher_lite;

import com.wdiscute.starcatcher_lite.registry.SCLSweetSpotsBehaviour;
import com.wdiscute.starcatcher_lite.registry.sweetspotbehaviour.AbstractSweetSpotBehaviour;
import com.wdiscute.starcatcher_lite.registry.*;
import com.wdiscute.utils.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.RegistryBuilder;

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

    public static Identifier rl(String s)
    {
        return Utils.rl(StarcatcherLite.MOD_ID, s);
    }

    public StarcatcherLite(IEventBus modEventBus, ModContainer modContainer)
    {
        SCLDataAttachments.register(modEventBus);
        SCLSweetSpotsBehaviour.register(modEventBus);
        SCLAttributes.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, SCLConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SYNCED, SCLConfig.SPEC_SERVER);
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
