package net.aolkzyeoer.draconagric.worldgen;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

public final class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_EMBERBLUE = createKey("ore_emberblue");

    private ModConfiguredFeatures() {
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(
                context,
                ORE_EMBERBLUE,
                Feature.ORE,
                new OreConfiguration(
                        new BlockMatchTest(Blocks.END_STONE),
                        ModBlocks.EMBERBLUE_ORE.get().defaultBlockState(),
                        4,
                        0.0F
                )
        );
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, name)
        );
    }
}
