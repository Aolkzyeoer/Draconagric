package net.aolkzyeoer.draconagric.block.entity;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Draconagric.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcanvilBlockEntity>> ARCANVIL_TABLE =
            BLOCK_ENTITY_TYPES.register("arcanvil_table", () -> BlockEntityType.Builder
                    .of(ArcanvilBlockEntity::new, ModBlocks.ARCANVIL_TABLE.get())
                    .build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
