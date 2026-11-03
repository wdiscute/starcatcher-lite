package com.wdiscute.starcatcher_lite.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.wdiscute.starcatcher_lite.fish.FishApi;
import com.wdiscute.starcatcher_lite.data.network.CBFishingStartedPayload;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public interface SCCommands
{
    static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context)
    {
        dispatcher.register(Commands.literal("starcatcher")
                .requires(sourceStack -> sourceStack.hasPermission(2))

                //starcatcher simulate_fish starcatcher:aurora
                .then(Commands.literal("simulate_fish")
                        .then(Commands.argument("item", ItemArgument.item(context))
                                .executes(c ->
                                        startMinigameFromItem(
                                                c.getSource().getPlayerOrException(),
                                                ItemArgument.getItem(c, "item").createItemStack(1, false)
                                        )
                                )
                        )
                )
        );
    }

    private static int startMinigameFromItem(ServerPlayer player, ItemStack item)
    {
        PacketDistributor.sendToPlayer(player,
                new CBFishingStartedPayload(item, FishApi.getDifficultyForItem(item.getItem())));
        return 0;
    }
}
