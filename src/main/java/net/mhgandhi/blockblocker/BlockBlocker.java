package net.mhgandhi.blockblocker;

import com.mojang.logging.LogUtils;
import net.mhgandhi.blockblocker.network.ModNetworking;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

//todo let creative mode do
//todo item blocker
//todo better command feedback
//todo better logging
//todo better command arguments

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BlockBlocker.MOD_ID)
public class BlockBlocker
{
    public static final String MOD_ID = "blockblocker";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BlockBlocker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(new ModEvents());
        ModNetworking.registerMessages();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
