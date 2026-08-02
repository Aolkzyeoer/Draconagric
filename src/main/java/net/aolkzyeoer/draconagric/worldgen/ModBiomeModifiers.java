package net.aolkzyeoer.draconagric.worldgen;

import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_EMBERBLUE_ORE = createKey("emberblue_ore");
    public static final TagKey<Biome> END_OUTER_ISLANDS = TagKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "end_outer_islands")
    );

    private ModBiomeModifiers() {
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderSet.Named<Biome> endBiomes = context.lookup(Registries.BIOME).getOrThrow(END_OUTER_ISLANDS);
        HolderSet.Direct<PlacedFeature> emberblueOre = HolderSet.direct(
                context.lookup(Registries.PLACED_FEATURE).getOrThrow(ModPlacedFeatures.ORE_EMBERBLUE)
        );
        context.register(
                ADD_EMBERBLUE_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        endBiomes,
                        emberblueOre,
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }

    private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, name)
        );
    }
}
