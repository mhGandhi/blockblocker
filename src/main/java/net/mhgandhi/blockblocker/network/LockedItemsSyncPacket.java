package net.mhgandhi.blockblocker.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class LockedItemsSyncPacket {
    private final List<String> lockedItems;

    public LockedItemsSyncPacket(List<String> lockedItems) {
        this.lockedItems = lockedItems;
    }

    public LockedItemsSyncPacket(FriendlyByteBuf buf) {
        this.lockedItems = buf.readList(FriendlyByteBuf::readUtf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(lockedItems, FriendlyByteBuf::writeUtf);
    }
        /*
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Update client-side data
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                CompoundTag data = player.getPersistentData();
                ListTag lockedItemsTag = new ListTag();
                for (String itemKey : lockedItems) {
                    lockedItemsTag.add(StringTag.valueOf(itemKey));
                }
                data.put("locked_items", lockedItemsTag);
            }
        });
        context.setPacketHandled(true);
    }
    */


}

