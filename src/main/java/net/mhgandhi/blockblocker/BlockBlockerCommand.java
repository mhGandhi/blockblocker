package net.mhgandhi.blockblocker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BlockBlockerCommand {
    public BlockBlockerCommand(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext buildContext){
        pDispatcher.register(
                Commands.literal("blockblocker")
                        .then(Commands.literal("sync"))
                        .executes(this::syncLock)
                        .then(Commands.literal("lock")
                                .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                        .executes(this::lockBlock)))
                        .then(Commands.literal("unlock")//todo only show locked blocks
                                .then(Commands.argument("block", BlockStateArgument.block(buildContext))
                                        .executes(this::unlockBlock)))
                        .then(Commands.literal("list")
                                .executes(this::listLockedBlocks))
        );
    }

    private int syncLock(CommandContext<CommandSourceStack> pContext) {
        CommandSourceStack source = pContext.getSource();
        Player player = extractPlayerTarget(pContext);
        if(player==null)return 0;

        LockedBlockManager.syncBlocked((ServerPlayer) player);
        return 1;
    }

    private int listLockedBlocks(CommandContext<CommandSourceStack> pContext) {
        CommandSourceStack source = pContext.getSource();
        Player player = extractPlayerTarget(pContext);
        if(player==null)return 0;

        List<Tag> blocks = LockedBlockManager.getLocked(player);
        if (blocks.isEmpty()) {
            source.sendSuccess(()->Component.literal("No locked blocks"), true);
        } else {
            source.sendSuccess(()->Component.literal(""+blocks), true);
        }
        return 1; // Success
    }

    private int unlockBlock(CommandContext<CommandSourceStack> pContext) {
        Block block = BlockStateArgument.getBlock(pContext, "block").getState().getBlock();
        CommandSourceStack source = pContext.getSource();
        Player player = extractPlayerTarget(pContext);
        if(player == null)return 0;

        if(!LockedBlockManager.removeBlockedBlock(player, block)){
            source.sendFailure(Component.literal(block.getName().getString()+ " is not locked"));
            return 0;
        }
        source.sendSuccess(()->Component.literal("Unlocked Block: " + block.getName().getString()), true);
        return 1; // Success
    }

    private int lockBlock(CommandContext<CommandSourceStack> pContext) {
        Block block = BlockStateArgument.getBlock(pContext, "block").getState().getBlock();
        CommandSourceStack source = pContext.getSource();
        Player player = extractPlayerTarget(pContext);
        if(player==null)return 0;

        if(!LockedBlockManager.addBlockedBlock(player, block)){
            source.sendFailure(Component.literal(block.getName().getString()+ " is already locked"));
            return 0;
        }
        source.sendSuccess(()->Component.literal("Locked Block: " + block.getName().getString()), true);
        return 1; // Success
    }

    private static Player extractPlayerTarget(CommandContext<CommandSourceStack> pContext){
        if(pContext.getSource().getEntity() instanceof Player p){
            return p;
        }else{
            pContext.getSource().sendFailure(Component.literal("This command can only be used on players."));
            return null;
        }
    }

}
