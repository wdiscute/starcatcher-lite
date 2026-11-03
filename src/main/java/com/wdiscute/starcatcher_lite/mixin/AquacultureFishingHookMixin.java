package com.wdiscute.starcatcher_lite.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.wdiscute.starcatcher_lite.fish.FishingMinigameMixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "com.teammetallurgy.aquaculture.entity.AquaFishingBobberEntity", remap = false)
public class AquacultureFishingHookMixin
{
    @Inject(method = "retrieve",
            at = @At(value = "NEW",
                    target = "net/neoforged/neoforge/event/entity/player/ItemFishedEvent",
                    ordinal = 0),
            cancellable = true)
    private void starcatcherLite$retrieve(ItemStack stack, CallbackInfoReturnable<Integer> cir, @Local(name = "lootEntries") LocalRef<List<ItemStack>> listRef)
    {
        FishingMinigameMixinHelper.handleRetrieve((FishingHook) (Object) this, listRef, cir);
    }

    @Inject(method = "catchingFish", at = @At("HEAD"), cancellable = true)
    private void starcatcherLite$freezeNibble(BlockPos pos, CallbackInfo ci)
    {
        if (FishingMinigameMixinHelper.shouldFreezeBite((FishingHook) (Object) this))
            ci.cancel();
    }
}