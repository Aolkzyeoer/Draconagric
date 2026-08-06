package net.aolkzyeoer.draconagric;

import com.mojang.logging.LogUtils;
import net.aolkzyeoer.draconagric.block.entity.ModBlockEntities;
import net.aolkzyeoer.draconagric.block.ModBlocks;
import net.aolkzyeoer.draconagric.component.ModDataComponents;
import net.aolkzyeoer.draconagric.effect.ModEffects;
import net.aolkzyeoer.draconagric.enchantment.ModEnchantments;
import net.aolkzyeoer.draconagric.event.DialogueEvents;
import net.aolkzyeoer.draconagric.event.SheepAggroEvents;
import net.aolkzyeoer.draconagric.item.ModCreativeModeTabs;
import net.aolkzyeoer.draconagric.item.ModItems;
import net.aolkzyeoer.draconagric.menu.ModMenuTypes;
import net.aolkzyeoer.draconagric.network.ModNetwork;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(Draconagric.MOD_ID)
public class Draconagric {
    public static final String MOD_ID = "draconagric";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Draconagric(IEventBus modEventBus, ModContainer modContainer) {
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModNetwork::register);
        modEventBus.addListener(this::addCreative);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new SheepAggroEvents());
        NeoForge.EVENT_BUS.register(new DialogueEvents());

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());
        Config.ITEM_STRINGS.get().forEach(item -> LOGGER.info("ITEM >> {}", item));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.DRAGON_HORN);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
