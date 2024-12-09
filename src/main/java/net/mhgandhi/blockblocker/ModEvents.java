package net.mhgandhi.blockblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = BlockBlocker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEvents {

    /**
     * intercepts block placing
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            Block placedBlock = event.getPlacedBlock().getBlock();
            if (LockedBlockManager.isBlocked(player, placedBlock)) {
                BlockBlocker.LOGGER.info("Blocked block placement: " + placedBlock.getDescriptionId() +
                        " by player " + player.getName().getString());
                event.setCanceled(true);
                player.displayClientMessage(Component.literal("NUH UH"), true);//todo translatable
            }
        }
    }

    /**
     * intercept placing on ClientSide
     */
    @SubscribeEvent
    public void onPlayerPlaceBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            if(event.getEntity() instanceof LocalPlayer player){
                if (player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
                    Block block = blockItem.getBlock();

                    if (LockedBlockManager.isBlocked(player, block)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     * intercepts tooltip display ClientSide
     */
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event){
        Player player = Minecraft.getInstance().player;
        ItemStack itemStack = event.getItemStack();
        if(player == null)return;
        if(itemStack.getItem() instanceof BlockItem blockItem){
            if (LockedBlockManager.isBlocked(player, blockItem.getBlock())){
                event.getToolTip().add(Component.literal("LOCKED"));
            }
        }
    }

    /**
     * makes sure everything is in order when a player logs in
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            LockedBlockManager.syncBlocked(serverPlayer);
        }
    }

    /**
     * register custom commands
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        new BlockBlockerCommand(event.getDispatcher(), event.getBuildContext());

        ConfigCommand.register(event.getDispatcher());
    }
}
