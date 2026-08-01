package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.enchantment.ModEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = "draconagric")
public class BridleTooltipEvent {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // 只有当前查看的物品带有 Bridle 附魔时，才显示提示
        int level = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.BRIDLE,
                stack
        );

        if (level > 0) {
            // 显示单件物品的生命值加成
            event.getToolTip().add(
                    Component.literal("§9Bridle: 已储存生命值 +" + level)
            );

            // 计算全套总生命值（前提是当前物品
            int totalHealth = calculateTotalBridleHealth(event);
            if (totalHealth > 0) {
                event.getToolTip().add(
                        Component.literal("§9全套总生命值: +" + totalHealth)
                );
            }
        }
    }

    private static int calculateTotalBridleHealth(ItemTooltipEvent event) {
        if (event.getEntity() == null) return 0;

        int total = 0;
        for (ItemStack armor : event.getEntity().getArmorSlots()) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(
                    ModEnchantments.BRIDLE,
                    armor
            );
            total += level;
        }
        return total;
    }
}
