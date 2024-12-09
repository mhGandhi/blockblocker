package net.mhgandhi.blockblocker.network;

import net.mhgandhi.blockblocker.BlockBlocker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.SimpleChannel;

public class ModNetworking {
    /*
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(BlockBlocker.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        int id = 0;
        CHANNEL.registerMessage(id++, LockedItemsSyncPacket.class, LockedItemsSyncPacket::toBytes, LockedItemsSyncPacket::new, LockedItemsSyncPacket::handle);
    }*/

}
