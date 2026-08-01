package net.aolkzyeoer.draconagric.item.tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

public class EmberBlueSword extends SwordItem {
    private static final String COMBO_KEY = "EmberblueSwordCombo";
    private static final String LAST_HIT_KEY = "EmberblueSwordLastHit";
    private static final int MAX_COMBO = 8;
    private static final int DECAY_TICKS = 100;
    private static final float BASE_ATTACK_SPEED = -2.4F;
    private static final float SPEED_PER_COMBO = 0.25F;

    private static final List<net.minecraft.core.Holder<MobEffect>> NEGATIVE_EFFECTS = List.of(
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.BLINDNESS,
            MobEffects.HUNGER,
            MobEffects.CONFUSION
    );

    private final Tier tier;

    public EmberBlueSword(Tier tier, Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, 10.0F, BASE_ATTACK_SPEED)));
        this.tier = tier;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 30;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (!attacker.level().isClientSide) {
            applyRandomNegativeEffect(target, attacker);
            increaseCombo(stack, attacker.level().getGameTime());
        }

        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide || !isSelected || !(entity instanceof Player)) {
            return;
        }

        CompoundTag tag = getOrCreateTag(stack);
        int combo = tag.getInt(COMBO_KEY);
        long lastHit = tag.getLong(LAST_HIT_KEY);

        if (combo > 0 && level.getGameTime() - lastHit > DECAY_TICKS) {
            tag.putInt(COMBO_KEY, combo - 1);
            tag.putLong(LAST_HIT_KEY, level.getGameTime());
            setTag(stack, tag);
            updateAttributes(stack, combo - 1);
        }
    }

    private void applyRandomNegativeEffect(LivingEntity target, LivingEntity attacker) {
        var effect = NEGATIVE_EFFECTS.get(attacker.getRandom().nextInt(NEGATIVE_EFFECTS.size()));
        int duration = 160 + attacker.getRandom().nextInt(141);
        target.addEffect(new MobEffectInstance(effect, duration, 0), attacker);
    }

    private void increaseCombo(ItemStack stack, long gameTime) {
        CompoundTag tag = getOrCreateTag(stack);
        int combo = Math.min(MAX_COMBO, tag.getInt(COMBO_KEY) + 1);
        tag.putInt(COMBO_KEY, combo);
        tag.putLong(LAST_HIT_KEY, gameTime);
        setTag(stack, tag);
        updateAttributes(stack, combo);
    }

    private CompoundTag getOrCreateTag(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag();
    }

    private void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private void updateAttributes(ItemStack stack, int combo) {
        float attackSpeed = BASE_ATTACK_SPEED + combo * SPEED_PER_COMBO;
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, SwordItem.createAttributes(tier, 10.0F, attackSpeed));
    }
}
