package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.effect.ModEffects;
import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Random;

@EventBusSubscriber(modid = Draconagric.MOD_ID)
public class KnightsGloryHandler {
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        // 获取攻击者
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        // 只对剑生效
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof SwordItem)) {
            return;
        }

        // 必须有骑士的荣光
        if (!player.hasEffect(ModEffects.KNIGHTS_GLORY)) {
            return;
        }

        // 伤害倍率
        float damage = event.getOriginalDamage();
        damage = damage * 1.2F;

        // 骑马额外1.5倍 + 回血 + 增益效果
        if (player.isPassenger()) {
            damage = damage * 1.5F;

            // 命中回血1点（只在骑马时）
            if (!player.level().isClientSide) {
                player.heal(1.0F);
            }

            // 随机正面buff 30秒（只在骑马时）
            applyRandomPositiveBuff(player);
        }

        event.setNewDamage(damage);
    }

    private static void applyRandomPositiveBuff(Player player) {
        List<Holder<MobEffect>> positiveEffects = List.of(
                MobEffects.MOVEMENT_SPEED,
                MobEffects.DIG_SPEED,
                MobEffects.DAMAGE_BOOST,
                MobEffects.JUMP,
                MobEffects.REGENERATION,
                MobEffects.FIRE_RESISTANCE,
                MobEffects.WATER_BREATHING,
                MobEffects.INVISIBILITY,
                MobEffects.NIGHT_VISION,
                MobEffects.HEALTH_BOOST,
                MobEffects.ABSORPTION,
                MobEffects.LUCK,
                MobEffects.DOLPHINS_GRACE,
                MobEffects.CONDUIT_POWER,
                MobEffects.DAMAGE_RESISTANCE
        );

        Holder<MobEffect> effect = positiveEffects.get(RANDOM.nextInt(positiveEffects.size()));
        player.addEffect(new MobEffectInstance(effect, 600, 0));
    }
}
