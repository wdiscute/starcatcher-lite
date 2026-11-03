package com.wdiscute.starcatcher_lite.minigame;

import com.mojang.logging.LogUtils;
import com.wdiscute.starcatcher_lite.SCConfig;
import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.registry.SCAttributes;
import com.wdiscute.starcatcher_lite.fish.Difficulty;
import com.wdiscute.starcatcher_lite.registry.sweetspotbehaviour.AbstractSweetSpotBehaviour;
import com.wdiscute.utils.ScreenUtils;
import net.minecraft.client.Minecraft;

import java.util.*;
import java.util.function.Supplier;

public class ActiveSweetSpot
{
    //from ss
    public final AbstractSweetSpotBehaviour behaviour;
    public final Difficulty.SweetSpot baseSS;
    public int thickness;
    public ScreenUtils.Image texture;
    public int reward;
    public int particleColor;

    //from minigame screen/rod
    public final FishingMinigameScreen instance;

    //from fp
    public boolean isFlip;
    public float vanishingRate;
    public float movingRate;

    //active sweet spot tracking
    public float alpha;
    public float pos;
    public int currentRotation;

    public boolean removed = false;
    public boolean shouldSudokuOnVanish = false;

    public int ticksActive = 0;
    public boolean canHit = true;

    // For use with modifiers, map an id with some data
    public Map<Integer, Object> extraData = new HashMap<>();

    public ActiveSweetSpot(FishingMinigameScreen instance, Difficulty.SweetSpot ss)
    {
        //get sweet spot type from rl
        Optional<Supplier<? extends AbstractSweetSpotBehaviour>> behaviour = Minecraft.getInstance().level.registryAccess().registryOrThrow(StarcatcherLite.SWEETSPOT_BEHAVIOUR).getOptional(ss.sweetSpotType());

        //if sweet spot type is registered then continue, otherwise set as removed
        if (behaviour.isPresent())
            this.behaviour = behaviour.get().get();
        else
        {
            this.behaviour = null;
            LogUtils.getLogger().error("The sweet-spot type {} is not registered, as such the sweet-spot has not been added", ss.sweetSpotType());
            removed = true;
        }

        this.instance = instance;

        this.baseSS = ss;
        this.texture = ss.texturePath();
        this.thickness = ss.size();
        this.reward = ss.reward();
        this.particleColor = ss.particleColor();

        this.isFlip = ss.isFlip();
        this.vanishingRate = (float) (ss.vanishingRate() * Minecraft.getInstance().player.getAttributeValue(SCAttributes.VANISHING_RATE_MULTIPLIER) * SCConfig.VANISHING_RATE_MULTIPLIER.get());
        this.movingRate = (float) (ss.movingRate() * SCConfig.MOVING_SPEED_MULTIPLIER.get());

        currentRotation = -1;

        this.alpha = 1;
    }
}
