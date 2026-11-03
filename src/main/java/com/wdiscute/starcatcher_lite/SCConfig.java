package com.wdiscute.starcatcher_lite;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SCConfig
{
    private static final ModConfigSpec.Builder BUILDER_CLIENT = new ModConfigSpec.Builder();

    //minigame & overlays positioning
    public static final ModConfigSpec.DoubleValue MINIGAME_X_OFFSET = BUILDER_CLIENT
            .push("ui_settings")
            .translation("starcatcher_lite.configuration.minigame_x_offset")
            .defineInRange("minigame_x_offset", 0d, -10000, 10000);

    public static final ModConfigSpec.DoubleValue MINIGAME_Y_OFFSET = BUILDER_CLIENT
            .translation("starcatcher_lite.configuration.minigame_y_offset")
            .defineInRange("minigame_y_offset", 0d, -10000, 10000);

    public static final ModConfigSpec.DoubleValue MINIGAME_RENDER_SCALE = BUILDER_CLIENT
            .translation("starcatcher_lite.configuration.minigame_scale")
            .defineInRange("minigame_scale", 1.5, 0.1, 6);

    public static final ModConfigSpec.DoubleValue HIT_DELAY = BUILDER_CLIENT
            .pop()
            .translation("starcatcher_lite.configuration.hit_delay")
            .defineInRange("hit_delay", 0.0d, -20, 20);

    public static final ModConfigSpec.BooleanValue DEBUG_MINIGAME = BUILDER_CLIENT
            .comment("Shows a bunch of extra text info during the minigame for debugging")
            .translation("starcatcher_lite.configuration.debug")
            .define("debug", false);

    public static final ModConfigSpec.BooleanValue ENABLE_HIT_SOUNDS = BUILDER_CLIENT
            .comment("Should sweet-spots play the hit sounds")
            .translation("starcatcher_lite.configuration.enable_hit_sounds")
            .define("enable_hit_sounds", true);

    public static final ModConfigSpec.BooleanValue ENABLE_MISS_SOUND = BUILDER_CLIENT
            .comment("Should play the hit sound")
            .translation("starcatcher_lite.configuration.enable_miss_sound")
            .define("enable_miss_sound", true);


    static final ModConfigSpec SPEC = BUILDER_CLIENT.build();


    private static final ModConfigSpec.Builder BUILDER_SERVER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue VANISHING_RATE_MULTIPLIER = BUILDER_SERVER
            .comment("Adjusts the vanishing rate, useful if you want to adjust the fishes' difficulty globally.")
            .translation("starcatcher_lite.configuration.vanishing_rate_multiplier")
            .defineInRange("vanishing_rate_multiplier", 1d, 0d, 100d);

    public static final ModConfigSpec.DoubleValue MOVING_SPEED_MULTIPLIER = BUILDER_SERVER
            .comment("Adjusts the moving sweet-spots speed, useful if you want to adjust the fishes' difficulty globally.")
            .translation("starcatcher_lite.configuration.moving_speed_multiplier")
            .defineInRange("moving_speed_multiplier", 1d, 0d, 100d);

    public static final ModConfigSpec.DoubleValue PENALTY_MULTIPLIER = BUILDER_SERVER
            .comment("Adjusts the penalty for missing rate, useful if you want to adjust the fishes' difficulty globally.")
            .translation("starcatcher_lite.configuration.penalty_multiplier")
            .defineInRange("penalty_multiplier", 1d, 0d, 100d);

    public static final ModConfigSpec.DoubleValue DECAY_RATE_MULTIPLIER = BUILDER_SERVER
            .comment("Adjusts the fish decay rate, useful if you want to adjust the fishes' difficulty globally.")
            .translation("starcatcher_lite.configuration.decay_multiplier")
            .defineInRange("decay_multiplier", 1d, 0d, 100d);

    public static final ModConfigSpec.DoubleValue HP_RATE_MULTIPLIER = BUILDER_SERVER
            .comment("Adjusts the fish's HP, useful if you want to adjust the fishes' difficulty globally.")
            .translation("starcatcher_lite.configuration.hp_multiplier")
            .defineInRange("hp_multiplier", 1d, 0d, 100d);

    public static final ModConfigSpec.DoubleValue HANDLE_SPEED_MULTIPLIER = BUILDER_SERVER
            .comment("Adjusts the handle speed, useful if you want to adjust the fishes' difficulty globally.")
            .translation("starcatcher_lite.configuration.handle_speed_multiplier")
            .defineInRange("handle_speed_multiplier", 1d, 0d, 100d);

    static final ModConfigSpec SPEC_SERVER = BUILDER_SERVER.build();

}
