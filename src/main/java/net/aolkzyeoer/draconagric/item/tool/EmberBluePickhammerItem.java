package net.aolkzyeoer.draconagric.item.tool;

import net.aolkzyeoer.draconagric.item.tier.EmberBlueTier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class EmberBluePickhammerItem extends PickaxeItem {


    public EmberBluePickhammerItem(Item.Properties properties) {

        super(
                EmberBlueTier.EMBERBLUE,
                properties.attributes(
                        PickaxeItem.createAttributes(
                                EmberBlueTier.EMBERBLUE,
                                7,
                                -2.8F

                        )
                )
        );

    }


    // 显示附魔光效
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
    @Override
    public int getEnchantmentValue() {
        return 30;
    }

}