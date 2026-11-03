package com.wdiscute.starcatcher_lite.registry;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public interface SCKeymappings
{
    KeyMapping MINIGAME_HIT = new KeyMapping("key.starcatcher_lite.minigame_hit", GLFW.GLFW_KEY_SPACE, "key.category.starcatcher_lite.starcatcher_lite");
}
