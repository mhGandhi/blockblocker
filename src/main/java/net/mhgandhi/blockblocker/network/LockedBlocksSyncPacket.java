package net.mhgandhi.blockblocker.network;

import net.mhgandhi.blockblocker.LockedBlockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.List;

public class LockedBlocksSyncPacket {
    private final List<String> lockedItems;

    public LockedBlocksSyncPacket(List<String> lockedItems) {
        this.lockedItems = lockedItems;
    }

    public LockedBlocksSyncPacket(FriendlyByteBuf buf) {
        this.lockedItems = buf.readList(FriendlyByteBuf::readUtf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(lockedItems, FriendlyByteBuf::writeUtf);
    }

    public void handle(CustomPayloadEvent.Context pContext) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            CompoundTag data = player.getPersistentData();
            ListTag lockedItemsTag = new ListTag();
            for (String itemKey : lockedItems) {
                lockedItemsTag.add(StringTag.valueOf(itemKey));
            }
            data.put(LockedBlockManager.BLOCKED_BOCKS_COLLECTION_NBT_KEY, lockedItemsTag);
        }
        pContext.setPacketHandled(true);

    }

}

