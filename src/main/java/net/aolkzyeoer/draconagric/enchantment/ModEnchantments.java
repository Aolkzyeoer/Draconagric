package net.aolkzyeoer.draconagric.enchantment;

import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModEnchantments {


    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(
                    net.minecraft.core.registries.Registries.ENCHANTMENT,
                    Draconagric.MOD_ID
            );


    public static final DeferredHolder<Enchantment, Enchantment> BRIDLE =
            ENCHANTMENTS.register(
                    "bridle",
                    () -> BridleEnchantment.create(
                            HolderSet.direct(
                                    BuiltInRegistries.ITEM.getHolderOrThrow(
                                            ResourceKey.create(
                                                    BuiltInRegistries.ITEM.key(),
                                                    ResourceLocation.withDefaultNamespace("leather_chestplate")
                                            )
                                    )
                            )
                    )
            );

}
