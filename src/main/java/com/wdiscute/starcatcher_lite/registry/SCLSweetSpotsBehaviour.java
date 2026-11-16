package com.wdiscute.starcatcher_lite.registry;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.registry.sweetspotbehaviour.AbstractSweetSpotBehaviour;
import com.wdiscute.starcatcher_lite.registry.sweetspotbehaviour.NormalSweetSpotBehaviour;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface SCLSweetSpotsBehaviour
{
    DeferredRegister<Supplier<? extends AbstractSweetSpotBehaviour>> REGISTRY =
            DeferredRegister.create(StarcatcherLite.SWEETSPOT_BEHAVIOUR_REGISTRY, StarcatcherLite.MOD_ID);

    Identifier NORMAL = registerSweetspot("normal", () -> NormalSweetSpotBehaviour::new);

    static Identifier registerSweetspot(String name, Supplier<Supplier<? extends AbstractSweetSpotBehaviour>> supplier)
    {
        REGISTRY.register(name, supplier);
        return StarcatcherLite.rl(name);
    }


    static void register(IEventBus eventBus)
    {
        REGISTRY.register(eventBus);
    }
}
