package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.effect.ModEffects;
import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;

@EventBusSubscriber(modid = "draconagric")
public class LapisEnchantmentEvents {
    private static final int EXTRA_DURABILITY = 300;
    private static final double EXTRA_DAMAGE = 1.0D;
    private static final ResourceLocation EXTRA_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "lapis_extra_damage");

    @SubscribeEvent
    public static void onPlayerEnchantItem(PlayerEnchantItemEvent event) {
        Player player = event.getEntity();
        if (player == null) return;
        if (!player.hasEffect(ModEffects.LAPIS_ENCHANTMENT)) return;

        ItemStack item = event.getEnchantedItem();
        if (item.isEmpty()) return;

        // 获取或创建 CustomData
        CustomData customData = item.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag;
        if (customData != null) {
            tag = customData.copyTag(); // 复制一份，避免修改原数据
        } else {
            tag = new CompoundTag();
        }

        if (!tag.getBoolean("draconagric_boosted")) {
            tag.putInt("draconagric_extra_durability", EXTRA_DURABILITY);
            tag.putDouble("draconagric_extra_damage", EXTRA_DAMAGE);
            tag.putBoolean("draconagric_boosted", true);

            Integer maxDamage = item.get(DataComponents.MAX_DAMAGE);
            if (maxDamage != null) {
                item.set(DataComponents.MAX_DAMAGE, maxDamage + EXTRA_DURABILITY);
            }

            ItemAttributeModifiers modifiers = item.getOrDefault(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    ItemAttributeModifiers.EMPTY
            );
            item.set(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    modifiers.withModifierAdded(
                            Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(
                                    EXTRA_DAMAGE_ID,
                                    EXTRA_DAMAGE,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.MAINHAND
                    )
            );
        }

        // 将 CustomData 设置回物品
        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        item.set(DataComponents.RARITY, Rarity.RARE);
    }
}
