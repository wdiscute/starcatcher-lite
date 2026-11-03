package com.wdiscute.starcatcher_lite.data.network;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.fish.Difficulty;
import com.wdiscute.starcatcher_lite.minigame.FishingMinigameScreen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CBFishingStartedPayload(ItemStack fish, Difficulty dif) implements CustomPacketPayload
{
    public static final Type<CBFishingStartedPayload> TYPE = new Type<>(StarcatcherLite.rl("fishing_started"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CBFishingStartedPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, CBFishingStartedPayload::fish,
            Difficulty.STREAM_CODEC, CBFishingStartedPayload::dif,
            CBFishingStartedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public void handle(IPayloadContext context)
    {
        FishingMinigameScreen.start(fish, dif);
    }
}
