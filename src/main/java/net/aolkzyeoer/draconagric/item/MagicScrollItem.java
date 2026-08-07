package net.aolkzyeoer.draconagric.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MagicScrollItem extends Item {
    private final String tooltipKey;

    public MagicScrollItem(Properties properties, String tooltipKey) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
    }
}
