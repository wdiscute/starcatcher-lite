package com.wdiscute.starcatcher_lite.registry.sweetspotbehaviour;

import com.wdiscute.starcatcher_lite.SCConfig;
import com.wdiscute.starcatcher_lite.minigame.ActiveSweetSpot;
import com.wdiscute.starcatcher_lite.minigame.FishingMinigameScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public class NormalSweetSpotBehaviour extends AbstractSweetSpotBehaviour
{
    @Override
    public void onHit(FishingMinigameScreen instance, ActiveSweetSpot ass)
    {
        super.onHit(instance, ass);
        instance.addParticles(ass.pos, 30, ass.particleColor);
        instance.progress += ass.reward;
        if (ass.isFlip) instance.currentRotation *= -1;
        ass.pos = instance.getRandomFreePosition(ass.thickness);
        if (SCConfig.ENABLE_HIT_SOUNDS.get())
            Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.3f, 1f);
        ass.alpha = 1;
    }
}
