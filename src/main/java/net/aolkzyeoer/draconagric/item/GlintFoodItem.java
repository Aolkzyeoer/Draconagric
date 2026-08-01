package net.aolkzyeoer.draconagric.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GlintFoodItem extends Item {
    public GlintFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
