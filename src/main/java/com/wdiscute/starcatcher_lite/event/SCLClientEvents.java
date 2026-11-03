package com.wdiscute.starcatcher_lite.event;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.minigame.KonamiDetector;
import com.wdiscute.starcatcher_lite.registry.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = StarcatcherLite.MOD_ID, value = Dist.CLIENT)
public class SCLClientEvents
{
    @SubscribeEvent
    public static void keyPressed(InputEvent.Key event)
    {
        if (event.getAction() == 1)
            KonamiDetector.keyPressed(event.getKey());
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(SCKeymappings.MINIGAME_HIT);
    }
}
