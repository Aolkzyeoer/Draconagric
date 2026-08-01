package net.aolkzyeoer.draconagric.enchantment;

import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.component.DataComponentMap;


public class BridleEnchantment {

    public static Enchantment create(HolderSet<Item> items) {

        return new Enchantment(

                Component.translatable(
                        "enchantment.draconagric.bridle"
                ),

                Enchantment.definition(
                        items,
                        10,
                        3,
                        Enchantment.constantCost(1),
                        Enchantment.constantCost(10),
                        2,
                        EquipmentSlotGroup.BODY
                ),

                HolderSet.direct(),

                DataComponentMap.EMPTY
        );
    }


    public static int getStoredHealth(int level) {
        return level;
    }
}
