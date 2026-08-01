package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.enchantment.ModEnchantments;
import net.aolkzyeoer.draconagric.item.ModCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = Draconagric.MOD_ID)
public class CreativeTabEvents {

    private static final ResourceKey<CreativeModeTab> DRACONAGRIC_TAB_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "draconagric_tab"));

    @SubscribeEvent
    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> tabKey = event.getTabKey();

        // 同时添加到原版原材料物品栏和你的物品栏
        if (tabKey == CreativeModeTabs.INGREDIENTS || tabKey == DRACONAGRIC_TAB_KEY) {
            addBridleBook(event);
        }
    }

    private static void addBridleBook(BuildCreativeModeTabContentsEvent event) {
        var enchantmentLookup = event.getParameters().holders().lookup(Registries.ENCHANTMENT);
        if (enchantmentLookup.isEmpty()) return;

        ResourceKey<net.minecraft.world.item.enchantment.Enchantment> enchantmentKey =
                ResourceKey.create(Registries.ENCHANTMENT,
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "bridle"));

        var holder = enchantmentLookup.get().get(enchantmentKey);
        if (holder.isEmpty()) return;

        EnchantmentInstance inst = new EnchantmentInstance(holder.get(), 3);
        ItemStack book = EnchantedBookItem.createForEnchantment(inst);

        try {
            event.accept(book);
        } catch (IllegalArgumentException e) {
            // 如果该物品已经在物品栏中存在（例如被其他模组添加），则忽略本次添加，避免崩溃
            // 这是正常情况，不需要打印日志
        }
    }
}
