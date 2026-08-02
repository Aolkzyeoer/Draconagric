package net.aolkzyeoer.draconagric.item.tool;

import net.aolkzyeoer.draconagric.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class EmberBlueHoe extends HoeItem {
    public EmberBlueHoe(Tier tier, Properties properties) {
        super(tier, properties.attributes(DiggerItem.createAttributes(tier, 2.0F, -1.0F)));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!HoeItem.onlyIfAirAbove(context)) {
            return InteractionResult.PASS;
        }

        Block block = state.getBlock();
        if (block != Blocks.GRASS_BLOCK && block != Blocks.DIRT_PATH && block != Blocks.DIRT) {
            return super.useOn(context);
        }

        Player player = context.getPlayer();
        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide) {
            BlockState farmland = ModBlocks.EMBERBLUE_FARMLAND.get().defaultBlockState();
            level.setBlock(pos, farmland, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, farmland));
            if (player != null) {
                context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 30;
    }
}
