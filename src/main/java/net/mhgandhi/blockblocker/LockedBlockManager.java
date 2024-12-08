package net.mhgandhi.blockblocker;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class LockedBlockManager {
    public static final String BLOCKED_BOCKS_COLLECTION_NBT_KEY = "bb_lockedBlocks";

    /**
     * Adds a block to the player's restricted collection.
     */
    public static void addBlockedBlock(Player player, Block block) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);

        String blockKey = block.toString();

        if (!blockedList.contains(StringTag.valueOf(blockKey))) {
            blockedList.add(StringTag.valueOf(blockKey));
            persistentData.put(BLOCKED_BOCKS_COLLECTION_NBT_KEY, blockedList);
            BlockBlocker.LOGGER.info("Added blocked block: " + blockKey + " for player " + player.getName().getString());
        }
    }

    /**
     * Removes a block from the player's restricted collection.
     */
    public static void removeBlockedBlock(Player player, Block block) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);

        String blockKey = block.toString();
        blockedList.removeIf(tag -> tag.getAsString().equals(blockKey));

        persistentData.put(BLOCKED_BOCKS_COLLECTION_NBT_KEY, blockedList);
        BlockBlocker.LOGGER.info("Removed blocked block: " + blockKey + " for player " + player.getName().getString());
    }

    /**
     * Checks if a block is restricted for the player.
     */
    public static boolean isBlocked(Player player, Block block) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);

        String blockKey = block.toString();
        for (Tag element : blockedList) {
            if (element.getAsString().equals(blockKey)) {
                return true;
            }
        }
        return false;
    }

    public static void syncBlocked(ServerPlayer pSp){
        CompoundTag persistentData = pSp.getPersistentData();
        if (!persistentData.contains(LockedBlockManager.BLOCKED_BOCKS_COLLECTION_NBT_KEY)) {
            persistentData.put(LockedBlockManager.BLOCKED_BOCKS_COLLECTION_NBT_KEY, new ListTag());
        }
        BlockBlocker.LOGGER.info("Synced persistent data for player " + pSp.getName().getString());
    }
}
