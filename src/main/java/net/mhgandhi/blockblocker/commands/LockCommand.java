package net.mhgandhi.blockblocker.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mhgandhi.blockblocker.LockedBlockManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class LockCommand {
    public LockCommand(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext buildContext){
        pDispatcher.register(
                Commands.literal("blockblocker")
                        .then(Commands.literal("lock")
                                .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                        .then(Commands.argument("target", EntityArgument.player()))
                                        .executes(this::lockBlock)))
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                        .then(Commands.argument("target", EntityArgument.player()))
                                        .executes(this::unlockBlock)))
                        .then(Commands.literal("list")
                                .then(Commands.argument("target", EntityArgument.player()))
                                .executes(this::listLockedBlocks))
        );
    }

    private int listLockedBlocks(CommandContext<CommandSourceStack> pContext) {
        CommandSourceStack source = pContext.getSource();
        Entity target;
        try {
            target = EntityArgument.getEntity(pContext,"target");
        } catch (CommandSyntaxException e) {
            target = source.getEntity();
        }

        if (target instanceof Player player) {
            List<Tag> blocks = LockedBlockManager.getLocked(player);

            if (blocks.isEmpty()) {
                source.sendSuccess(()->Component.literal("Your restricted list is empty."), true);
            } else {
                source.sendSuccess(()->Component.literal("Restricted blocks: " + blocks), true);
            }
            return 1; // Success
        } else {
            source.sendFailure(Component.literal("This command can only be used on players."));
            return 0; // Failure
        }
    }

    private int unlockBlock(CommandContext<CommandSourceStack> pContext) {
        Block block = BlockStateArgument.getBlock(pContext, "block").getState().getBlock();
        CommandSourceStack source = pContext.getSource();
        Entity target;
        try {
            target = EntityArgument.getEntity(pContext,"target");
        } catch (CommandSyntaxException e) {
            target = source.getEntity();
        }

        if (target instanceof Player player) {
            LockedBlockManager.removeBlockedBlock(player, block);
            source.sendSuccess(()->Component.literal("Removed block from your restricted list: " + block.getName().getString()), true);
            return 1; // Success
        } else {
            source.sendFailure(Component.literal("This command can only be used on players."));
            return 0; // Failure
        }
    }

    private int lockBlock(CommandContext<CommandSourceStack> pContext) {
        Block block = BlockStateArgument.getBlock(pContext, "block").getState().getBlock();
        CommandSourceStack source = pContext.getSource();
        Entity target;
        try {
            target = EntityArgument.getEntity(pContext,"target");
        } catch (CommandSyntaxException e) {
            target = source.getEntity();
        }


        if (target instanceof Player player) {
            LockedBlockManager.addBlockedBlock(player, block);
            source.sendSuccess(()->Component.literal("Added block to your restricted list: " + block.getName().getString()), true);
            return 1; // Success
        } else {
            source.sendFailure(Component.literal("This command can only be used on players."));
            return 0; // Failure
        }
    }

}
