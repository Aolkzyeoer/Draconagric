package net.aolkzyeoer.draconagric.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
public class ModFoodItem extends Item {
    private final FoodProperties foodProperties;

    public ModFoodItem(Properties properties, FoodProperties food) {
        super(properties);
        this.foodProperties = food;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        return new ItemStack(Items.BOWL);
    }

    @Override
    public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
        return this.foodProperties;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;  // 使用原版进食动作
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;  // 原版食物标准时长
    }
}
