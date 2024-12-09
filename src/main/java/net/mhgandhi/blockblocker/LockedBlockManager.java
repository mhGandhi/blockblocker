package net.mhgandhi.blockblocker;

import net.mhgandhi.blockblocker.network.LockedItemsSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.mhgandhi.blockblocker.network.ModNetworking;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class LockedBlockManager {
    public static final String BLOCKED_BOCKS_COLLECTION_NBT_KEY = "bb_lockedBlocks";

    /**
     * Adds a block to the player's restricted collection.
     *
     * @return
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
        return unlockedBefore;
    }

    /**
     * Removes a block from the player's restricted collection.
     */
    public static boolean removeBlockedBlock(Player player, Block block) {
        boolean lockedBefore = isBlocked(player, block);
        CompoundTag persistentData = player.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);

        String blockKey = block.toString();
        blockedList.removeIf(tag -> tag.getAsString().equals(blockKey));

        persistentData.put(BLOCKED_BOCKS_COLLECTION_NBT_KEY, blockedList);
        BlockBlocker.LOGGER.info("Removed blocked block: " + blockKey + " for player " + player.getName().getString());
        return lockedBefore;
    }

    /**
     * Checks if a block is restricted for the player.
     */
    public static boolean isBlocked(Player player, Block block) {
        CompoundTag persistentData = player.getPersistentData();
        System.out.println(persistentData);
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);
        System.out.println("\ttesting for "+block.toString()+" in blockedList size "+ blockedList.size());
        String blockKey = block.toString();
        for (Tag element : blockedList) {
            System.out.println();
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

    public static List<Tag> getLocked(Player pPlayer) {
        CompoundTag persistentData = pPlayer.getPersistentData();
        ListTag blockedList = persistentData.getList(BLOCKED_BOCKS_COLLECTION_NBT_KEY, Tag.TAG_STRING);
        return blockedList.stream().toList();
    }

    public static void sendLockedItemsToClient(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag lockedItemsTag = persistentData.getList("locked_items", Tag.TAG_STRING);

        List<String> lockedItems = new ArrayList<>();
        for (Tag itemTag : lockedItemsTag) {
            lockedItems.add(itemTag.getAsString());
        }

        //todo ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new LockedItemsSyncPacket(lockedItems));
    }
}
