package com.wdiscute.starcatcher_lite.data.network;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.fish.FishApi;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SBFishingCompletedPayload(boolean completed) implements CustomPacketPayload
{
    public static final Type<SBFishingCompletedPayload> TYPE = new Type<>(StarcatcherLite.rl("fishing_completed"));

    public static final StreamCodec<ByteBuf, SBFishingCompletedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SBFishingCompletedPayload::completed,
            SBFishingCompletedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public void handle(IPayloadContext context)
    {
        context.enqueueWork( () ->
                FishApi.playerCompletedMinigame(((ServerPlayer) context.player()), completed));
    }
}
