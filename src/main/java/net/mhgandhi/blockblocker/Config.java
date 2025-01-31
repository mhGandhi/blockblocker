package net.mhgandhi.blockblocker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = BlockBlocker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_STRINGS = BUILDER
            .comment("A list of blocks that are blocked for all new players on the world")
            .defineListAllowEmpty("items", List.of("minecraft:iron_block","minecraft:sand"), Config::validateBlockName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static Set<Block> defaultLockedBlocks;

    private static boolean validateBlockName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.BLOCKS.containsKey(ResourceLocation.tryParse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultLockedBlocks = BLOCK_STRINGS.get().stream()
                .map(itemName -> ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(itemName)))
                .collect(Collectors.toSet());
    }
}
