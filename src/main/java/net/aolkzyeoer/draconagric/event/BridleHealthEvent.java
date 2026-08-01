package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.effect.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class BridleHealthEvent {

    private static final ResourceLocation BRIDLE_HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath("draconagric", "bridle_health");

    /**
     * 玩家登录或重生时触发，确保属性正确应用
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        updateBridleHealth(event.getEntity());
    }

    /**
     * 玩家装备变更时触发（穿戴/脱下盔甲）
     */
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            updateBridleHealth(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!player.hasEffect(ModEffects.LAPIS_ENCHANTMENT)) return;
        if (getTotalBridleLevel(player) <= 0) return;

        MobEffectInstance regeneration = player.getEffect(MobEffects.REGENERATION);
        if (regeneration == null || regeneration.getDuration() <= 40) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 220, 0, false, true, true));
        }
    }

    /**
     * 核心更新方法
     */
    private static void updateBridleHealth(Player player) {
        // 客户端不处理
        if (player.level().isClientSide()) return;

        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) return;

        // 移除旧修饰器
        attribute.removeModifier(BRIDLE_HEALTH_ID);

        // 计算所有盔甲上的 Bridle 附魔总等级
        int totalLevel = getTotalBridleLevel(player);

        if (totalLevel > 0) {
            // 每级增加 2 点生命值（1 颗心）
            attribute.addTransientModifier(
                    new AttributeModifier(
                            BRIDLE_HEALTH_ID,
                            totalLevel * 2.0,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );
        }
    }

    /**
     * 遍历所有盔甲槽位，累加 Bridle 附魔等级
     */
    private static int getTotalBridleLevel(Player player) {
        int total = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(armor);
            for (var entry : enchantments.entrySet()) {
                if (entry.getKey().unwrapKey().isPresent()) {
                    ResourceLocation id = entry.getKey().unwrapKey().get().location();
                    if (id.equals(ResourceLocation.fromNamespaceAndPath("draconagric", "bridle"))) {
                        total += entry.getIntValue();
                    }
                }
            }
        }
        return total;
    }
}
