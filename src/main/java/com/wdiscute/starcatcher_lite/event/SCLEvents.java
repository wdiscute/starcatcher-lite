package com.wdiscute.starcatcher_lite.event;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.registry.*;
import com.wdiscute.starcatcher_lite.data.network.*;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = StarcatcherLite.MOD_ID)
public class SCLEvents
{
    @SubscribeEvent
    public static void itemFished(ItemFishedEvent event)
    {

    }

    @SubscribeEvent
    public static void addCommand(RegisterCommandsEvent event)
    {
        SCCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void addRegistry(NewRegistryEvent event)
    {
        event.register(StarcatcherLite.SWEETSPOT_BEHAVIOUR_REGISTRY);
    }

    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event)
    {
        SCAttributes.REGISTRY.getEntries().forEach(o -> event.add(EntityType.PLAYER, o, 1.0));
    }

    @SubscribeEvent
    public static void registerDataMaps(RegisterDataMapTypesEvent event)
    {
        event.register(SCDataMaps.FISH_DIFFICULTY);
        event.register(SCDataMaps.MINIGAME_TEXTURES);
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                CBFishingStartedPayload.TYPE,
                CBFishingStartedPayload.STREAM_CODEC,
                CBFishingStartedPayload::handle
        );

        registrar.playToServer(
                SBFishingCompletedPayload.TYPE,
                SBFishingCompletedPayload.STREAM_CODEC,
                SBFishingCompletedPayload::handle
        );
    }
}
