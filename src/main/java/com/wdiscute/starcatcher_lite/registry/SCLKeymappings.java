package com.wdiscute.starcatcher_lite.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public interface SCLKeymappings
{
    KeyMapping.Category CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath("starcatcher_lite", "starcatcher_lite"));

    KeyMapping MINIGAME_HIT = new KeyMapping("key.starcatcher.minigame_hit", InputConstants.KEY_SPACE, CATEGORY);
}
