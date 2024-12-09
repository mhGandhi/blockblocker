package net.mhgandhi.blockblocker;

import net.mhgandhi.blockblocker.network.LockedBlocksSyncPacket;
import net.mhgandhi.blockblocker.network.ModNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class LockedBlockManager {
    public static final String BLOCKED_BOCKS_COLLECTION_NBT_KEY = "bb_lockedBlocks";

    /**
     * locks a block
     * @param player player to lock the block for
     * @param block block to lock
     * @return whether the operation resulted in any changes
     */
    public static boolean addBlockedBlock(Player player, Block block) {
        boolean unlockedBefore = !isBlocked(player,block);
        CompoundTag persistentData = player.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);

        String blockKey = block.toString();

        if (!blockedList.contains(StringTag.valueOf(blockKey))) {
            blockedList.add(StringTag.valueOf(blockKey));
            persistentData.put(BLOCKED_BOCKS_COLLECTION_NBT_KEY, blockedList);
            BlockBlocker.LOGGER.info("Added blocked block: " + blockKey + " for player " + player.getName().getString());
        }

        if(unlockedBefore)
            sendLockedBlocksToClient((ServerPlayer) player);
        return unlockedBefore;
    }

    /**
     * unlocks a block
     * @param player player to unlock the block for
     * @param block block to unlock
     * @return whether the operation resulted in any changes
     */
    public static boolean removeBlockedBlock(Player player, Block block) {
        boolean lockedBefore = isBlocked(player, block);
        CompoundTag persistentData = player.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);

        String blockKey = block.toString();
        blockedList.removeIf(tag -> tag.getAsString().equals(blockKey));

        persistentData.put(BLOCKED_BOCKS_COLLECTION_NBT_KEY, blockedList);
        BlockBlocker.LOGGER.info("Removed blocked block: " + blockKey + " for player " + player.getName().getString());

        if(lockedBefore)
            sendLockedBlocksToClient((ServerPlayer) player);
        return lockedBefore;
    }

    /**
     * check if block is locked
     * @param player player to test whether the block is locked for
     * @param block block to test whether it is locked
     * @return whether @block is locked for @player
     */
    public static boolean isBlocked(Player player, Block block) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);
        //System.out.println("\ttesting for "+block.toString()+" in blockedList size "+ blockedList.size());
        String blockKey = block.toString();
        for (Tag element : blockedList) {
            //System.out.println(element.getAsString()+ " == "+blockKey);
            if (element.getAsString().equals(blockKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * sync locked blocks to player and create persistent data tag if it doesn't exist
     * @param pSp player to sync
     */
    public static void syncBlocked(ServerPlayer pSp){
        CompoundTag persistentData = pSp.getPersistentData();
        if (!persistentData.contains(LockedBlockManager.BLOCKED_BOCKS_COLLECTION_NBT_KEY)) {
            //todo default locked config over extra class
            persistentData.put(LockedBlockManager.BLOCKED_BOCKS_COLLECTION_NBT_KEY, new ListTag());
        }
        sendLockedBlocksToClient(pSp);
        BlockBlocker.LOGGER.info("Synced persistent block locks for player " + pSp.getName().getString());
    }

    /**
     * get a list of NBT tags containing the locked blocks
     * @param pPlayer player to get the locked blocks for
     * @return list of nbt tags describing the blocks
     */
    public static List<Tag> getLocked(Player pPlayer) {
        CompoundTag persistentData = pPlayer.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);
        return blockedList.stream().toList();
    }

    /**
     * update the locked blocks list for the client
     * @param player player to update for
     */
    public static void sendLockedBlocksToClient(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag lockedItemsTag = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);
        List<String> lockedItems = new ArrayList<>();
        for (Tag itemTag : lockedItemsTag) {
            lockedItems.add(itemTag.getAsString());
        }
        ModNetworking.sendToPlayer(new LockedBlocksSyncPacket(lockedItems), player);
    }
}
