package com.wdiscute.starcatcher_lite.fish;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.wdiscute.starcatcher_lite.data.network.CBFishingStartedPayload;
import com.wdiscute.starcatcher_lite.registry.SCDataAttachments;
import com.wdiscute.utils.MaybeStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class FishingMinigameMixinHelper
{
    public static boolean shouldFreezeBite(FishingHook hook)
    {
        return hook.getPlayerOwner() instanceof ServerPlayer sp
               && sp.getData(SCDataAttachments.IN_FISHING_MINIGAME);
    }

    public static void handleRetrieve(FishingHook hook, LocalRef<List<ItemStack>> listRef, CallbackInfoReturnable<Integer> cir)
    {
        Player player = hook.getPlayerOwner();
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
