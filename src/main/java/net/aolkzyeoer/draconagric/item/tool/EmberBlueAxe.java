package net.aolkzyeoer.draconagric.item.tool;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class EmberBlueAxe extends AxeItem {

    public EmberBlueAxe(Tier tier, Properties properties) {
        super(tier, properties.attributes(
                DiggerItem.createAttributes(tier, 10.0F, -3.0F)
        ));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 30;
    }
}
