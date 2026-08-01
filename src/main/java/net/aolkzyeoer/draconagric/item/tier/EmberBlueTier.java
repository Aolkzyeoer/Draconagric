package net.aolkzyeoer.draconagric.item.tier;

import net.aolkzyeoer.draconagric.item.ModItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;


public enum EmberBlueTier implements Tier {


    EMBERBLUE;


    @Override
    public int getUses() {
        return 4096;
    }


    @Override
    public float getSpeed() {
        return 12.0F;
    }


    @Override
    public float getAttackDamageBonus() {
        return 0;
    }


    @Override
    public int getEnchantmentValue() {
        return 20;
    }


    @Override
    public Ingredient getRepairIngredient() {

        return Ingredient.of(
                ModItems.EMBERBLUE.get()
        );

    }


    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {

        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;

    }

}
