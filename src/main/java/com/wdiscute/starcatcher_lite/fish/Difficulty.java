package com.wdiscute.starcatcher_lite.fish;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.starcatcher_lite.registry.SCSweetSpotsBehaviour;
import com.wdiscute.utils.ExtraComposites;
import com.wdiscute.utils.ScreenUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public record Difficulty(
        int hp,
        int speed,
        int penalty,
        float decay,
        List<SweetSpot> sweetSpots
)
{
    public Difficulty(int hp, int speed, int penalty, float decay, SweetSpot... sweetSpots)
    {
        this(hp, speed, penalty, decay, Arrays.stream(sweetSpots).toList());
    }

    public static Difficulty TRASH = new Difficulty(
            100, 10, 0, 0,
            SweetSpot.TRASH, SweetSpot.TRASH
    );

    public static Difficulty EASY = new Difficulty(
            75, 7, 5, 1,
            SweetSpot.NORMAL, SweetSpot.NORMAL
    );

    public static Difficulty MEDIUM = new Difficulty(
            100, 10, 20, 1,
            SweetSpot.NORMAL, SweetSpot.THIN
    );

    public static Difficulty HARD = new Difficulty(
            100, 11, 20, 1,
            SweetSpot.THIN, SweetSpot.THIN);

    public static final Codec<Difficulty> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("hp").forGetter(Difficulty::hp),
                    Codec.INT.fieldOf("speed").forGetter(Difficulty::speed),
                    Codec.INT.fieldOf("missPenalty").forGetter(Difficulty::penalty),
                    Codec.FLOAT.fieldOf("decay").forGetter(Difficulty::decay),
                    SweetSpot.LIST_CODEC.fieldOf("sweetspots").forGetter(Difficulty::sweetSpots)
            ).apply(instance, Difficulty::new));

    public static final StreamCodec<FriendlyByteBuf, Difficulty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Difficulty::hp,
            ByteBufCodecs.INT, Difficulty::speed,
            ByteBufCodecs.INT, Difficulty::penalty,
            ByteBufCodecs.FLOAT, Difficulty::decay,
            SweetSpot.LIST_STREAM_CODEC, Difficulty::sweetSpots,
            Difficulty::new
    );

    public record SweetSpot(
            ResourceLocation sweetSpotType,
            ScreenUtils.Image texturePath,
            int size,
            int reward,
            boolean isFlip,
            float vanishingRate,
            float movingRate,
            int particleColor
    )
    {
        public SweetSpot(ResourceLocation sweetSpotType, ScreenUtils.Image texturePath, int size, int reward, int particleColor)
        {
            this(sweetSpotType, texturePath, size, reward, false, 0, 0, particleColor);
        }

        private static final ScreenUtils.Image RL_NORMAL = new ScreenUtils.Image(StarcatcherLite.rl("textures/gui/minigame/spots/normal.png"), 96, 96);
        private static final ScreenUtils.Image RL_THIN = new ScreenUtils.Image(StarcatcherLite.rl("textures/gui/minigame/spots/thin.png"), 96, 96);

        public SweetSpot vanishing(float vanishingRate)
        {
            return new SweetSpot(this.sweetSpotType, this.texturePath, this.size, this.reward, this.isFlip, vanishingRate, this.movingRate, this.particleColor);
        }

        public SweetSpot moving(float movingRate)
        {
            return new SweetSpot(this.sweetSpotType, this.texturePath, this.size, this.reward, this.isFlip, this.vanishingRate, movingRate, this.particleColor);
        }

        public static SweetSpot TRASH = new SweetSpot(
                SCSweetSpotsBehaviour.NORMAL,
                RL_NORMAL,
                22,
                30,
                0xff0ff000
        );

        public static SweetSpot NORMAL = new SweetSpot(
                SCSweetSpotsBehaviour.NORMAL,
                RL_NORMAL,
                22,
                15,
                0xff0ff000
        );

        public static SweetSpot THIN = new SweetSpot(
                SCSweetSpotsBehaviour.NORMAL,
                RL_THIN,
                15,
                20,
                0xff0ff000
        );

        public static final Codec<SweetSpot> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("sweetspot_type").forGetter(SweetSpot::sweetSpotType),
                        ScreenUtils.Image.codecFixedSize(96, 96).fieldOf("texture_path").forGetter(SweetSpot::texturePath),
                        Codec.INT.fieldOf("hitbox_size_in_pixels").forGetter(SweetSpot::size),
                        Codec.INT.fieldOf("reward").forGetter(SweetSpot::reward),
                        Codec.BOOL.fieldOf("is_flip").forGetter(SweetSpot::isFlip),
                        Codec.FLOAT.fieldOf("vanishing_rate").forGetter(SweetSpot::vanishingRate),
                        Codec.FLOAT.fieldOf("moving_rate").forGetter(SweetSpot::movingRate),
                        Codec.INT.fieldOf("color_as_int").forGetter(SweetSpot::particleColor)
                ).apply(instance, SweetSpot::new));

        public static final Codec<List<SweetSpot>> LIST_CODEC = CODEC.listOf();

        public static final StreamCodec<FriendlyByteBuf, SweetSpot> STREAM_CODEC = ExtraComposites.composite(
                ResourceLocation.STREAM_CODEC, SweetSpot::sweetSpotType,
                ScreenUtils.Image.STREAM_CODEC, SweetSpot::texturePath,
                ByteBufCodecs.INT, SweetSpot::size,
                ByteBufCodecs.INT, SweetSpot::reward,
                ByteBufCodecs.BOOL, SweetSpot::isFlip,
                ByteBufCodecs.FLOAT, SweetSpot::vanishingRate,
                ByteBufCodecs.FLOAT, SweetSpot::movingRate,
                ByteBufCodecs.INT, SweetSpot::particleColor,
                SweetSpot::new
        );

        public static final StreamCodec<FriendlyByteBuf, List<SweetSpot>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
    }
}
