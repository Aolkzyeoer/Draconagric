package net.aolkzyeoer.draconagric.component;

import com.mojang.serialization.Codec;
import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Draconagric.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> EXTRA_HEALTH =
            DATA_COMPONENT_TYPES.register(
                    "extra_health",
                    () -> DataComponentType.<Float>builder()
                            .persistent(Codec.FLOAT)
                            .networkSynchronized(ByteBufCodecs.FLOAT)
                            .build()
            );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
