package com.wdiscute.starcatcher_lite.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.wdiscute.starcatcher_lite.data.network.CBFishingStartedPayload;
import com.wdiscute.starcatcher_lite.fish.FishApi;
import com.wdiscute.starcatcher_lite.registry.SCDataAttachments;
import com.wdiscute.utils.MaybeStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin
{

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Inject(method = "catchingFish", at = @At("HEAD"), cancellable = true)
    private void starcatcherLite$freezeNibble(BlockPos pos, CallbackInfo ci)
    {
        FishingHook self = (FishingHook) (Object) this;

        if (self.getPlayerOwner() instanceof ServerPlayer sp && sp.getData(SCDataAttachments.IN_FISHING_MINIGAME))
        {
            ci.cancel();
        }
    }

    @Inject(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancements/critereon/FishingRodHookedTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/projectile/FishingHook;Ljava/util/Collection;)V",
                    ordinal = 1
            ),
            cancellable = true)
    private void starcatcherLite$retrieveFishingHookMixin(ItemStack stack, CallbackInfoReturnable<Integer> cir, @Local LocalRef<List<ItemStack>> listRef)
    {
        Player player = getPlayerOwner();
        if (player instanceof ServerPlayer sp)
        {
            //if in fishing minigame
            if (sp.getData(SCDataAttachments.IN_FISHING_MINIGAME))
            {
                listRef.set(sp.getData(SCDataAttachments.ITEMS_BEING_FISHED).stream().map(MaybeStack::toStack).toList());

                sp.removeData(SCDataAttachments.IN_FISHING_MINIGAME);
                sp.removeData(SCDataAttachments.ITEMS_BEING_FISHED);
            }
            else
            {
                List<ItemStack> stacks = listRef.get();

                //if no item is being fished, ignore
                if (stacks.isEmpty())
                    return;

                ItemStack fishStack = stacks.getFirst();

                //start minigame
                PacketDistributor.sendToPlayer(sp,
                        new CBFishingStartedPayload(fishStack, FishApi.getDifficultyForItem(fishStack.getItem())));

                //set data on player
                sp.setData(SCDataAttachments.IN_FISHING_MINIGAME, true);
                sp.setData(SCDataAttachments.ITEMS_BEING_FISHED, listRef.get().stream().map(MaybeStack::new).toList());

                cir.setReturnValue(0);
                cir.cancel();
            }
        }
    }
}