package net.mhgandhi.blockblocker;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.server.command.ConfigCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@Mod.EventBusSubscriber(modid = BlockBlocker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        /*// Some client setup code
        BlockBlocker.LOGGER.info("HELLO FROM CLIENT SETUP");
        BlockBlocker.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        */
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        //BlockBlocker.LOGGER.info("HELLO from server starting");
    }

    /*
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getPlacedBlock().getBlock() == Blocks.SAND) {
                event.setCanceled(true);
            }
        }
    }*/

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            Block placedBlock = event.getPlacedBlock().getBlock();
            if (LockedBlockManager.isBlocked(player, placedBlock)) {
                BlockBlocker.LOGGER.info("Blocked block placement: " + placedBlock.getDescriptionId() +
                        " by player " + player.getName().getString());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            LockedBlockManager.syncBlocked(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        new BlockBlockerCommand(event.getDispatcher(), event.getBuildContext());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event){
        //Player player = Minecraft.getInstance().player;
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        if(player == null)return;
        if(itemStack.getItem() instanceof BlockItem blockItem){
            if (LockedBlockManager.isBlocked(player, blockItem.getBlock())){
                event.getToolTip().add(Component.literal("LOCKED"));
            }
        }
    }
}
