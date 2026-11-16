package com.wdiscute.starcatcher_lite.event;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.registry.*;
import com.wdiscute.starcatcher_lite.data.network.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
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
        SCLCommands.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event)
    {
        if(event.getEntity() instanceof ServerPlayer sp && sp.fishing == null)
        {
            sp.removeData(SCLDataAttachments.ITEMS_BEING_FISHED);
            sp.removeData(SCLDataAttachments.IN_FISHING_MINIGAME);
        }
    }

    @SubscribeEvent
    public static void addRegistry(NewRegistryEvent event)
    {
        event.register(StarcatcherLite.SWEETSPOT_BEHAVIOUR_REGISTRY);
    }

    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event)
    {
        SCLAttributes.REGISTRY.getEntries().forEach(o -> event.add(EntityType.PLAYER, o, 1.0));
    }

    @SubscribeEvent
    public static void registerDataMaps(RegisterDataMapTypesEvent event)
    {
        event.register(SCLDataMaps.FISH_DIFFICULTY);
        event.register(SCLDataMaps.MINIGAME_TEXTURES);
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                SCLCBFishingStartedPayload.TYPE,
                SCLCBFishingStartedPayload.STREAM_CODEC,
                SCLCBFishingStartedPayload::handle
        );

        registrar.playToServer(
                SCLSBFishingCompletedPayload.TYPE,
                SCLSBFishingCompletedPayload.STREAM_CODEC,
                SCLSBFishingCompletedPayload::handle
        );
    }
}
