package net.aolkzyeoer.draconagric.item;

import net.aolkzyeoer.draconagric.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static  final FoodProperties FRIED_DRAGON_EGG_BURGER = new FoodProperties.Builder().nutrition(13).saturationModifier(0.7f)
        .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST,1400,2), 1.0f).build();
    public static final FoodProperties ANCIENT_DRAGON_EGG_LIQUID = new FoodProperties.Builder().nutrition(16).saturationModifier(0.5f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST,1200,0), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION,600,1), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,1400,1), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION,300,0), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS,200,0), 1.0f).build();
    public static  final FoodProperties DRAGON_EGG_SUSHI = new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build();
    public static  final FoodProperties SLICE_OF_DRAGON_EGG_CAKE = new FoodProperties.Builder().nutrition(3).saturationModifier(0.7f).build();
    public static  final FoodProperties DRAGON_EGG_SANDWICH = new FoodProperties.Builder().nutrition(11).saturationModifier(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION,1800,1), 1.0f).build();
    public static  final FoodProperties DRAGON_EGG_RICE_BOWL = new FoodProperties.Builder().nutrition(8).saturationModifier(0.5f).build();
    public static final FoodProperties HOLY_GOLD_KNIGHT_PIE = new FoodProperties.Builder().nutrition(18).saturationModifier(0.8f)
            .effect(() -> new MobEffectInstance(MobEffects.JUMP,3000,0),1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED,3000,1),1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,3000,0),1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED,3000,0),1.0f)
            .effect(() -> new MobEffectInstance(ModEffects.KNIGHTS_GLORY,12000,0),1.0f).build();
    public  static  final  FoodProperties LAPIS_FRIED_RICE = new FoodProperties.Builder().nutrition(14).saturationModifier(0.7f)
            .effect(() -> new MobEffectInstance(ModEffects.LAPIS_ENCHANTMENT,12000,0),1.0f).build();
    public static final  FoodProperties SHIT = new FoodProperties.Builder().nutrition(6).saturationModifier(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.BAD_OMEN,3000,0),1).build();
    public static final FoodProperties MANLANBA = new FoodProperties.Builder().nutrition(5).saturationModifier(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 1200, 0), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 8400, 0), 1.0f).build();
}
