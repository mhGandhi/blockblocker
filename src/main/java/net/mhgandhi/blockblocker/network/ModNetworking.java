package net.mhgandhi.blockblocker.network;

import net.mhgandhi.blockblocker.BlockBlocker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.List;

public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(
            ResourceLocation.fromNamespaceAndPath(BlockBlocker.MOD_ID, "main"))
                    .optionalClient()
                    .simpleChannel();

    public static void registerMessages() {
        CHANNEL.messageBuilder(LockedBlocksSyncPacket.class, 341, NetworkDirection.PLAY_TO_CLIENT)
                        .encoder(LockedBlocksSyncPacket::encode)
                        .decoder(LockedBlocksSyncPacket::new)
                        .consumerMainThread(LockedBlocksSyncPacket::handle)
                        .add();
    }

    public static void sendToPlayer(Object msg, ServerPlayer player){
        CHANNEL.send(msg, PacketDistributor.PLAYER.with(player));
    }

}
