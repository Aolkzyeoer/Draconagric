package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.item.ModFoodItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.aolkzyeoer.draconagric.item.ModItems;

public class SheepAggroEvents {

    public SheepAggroEvents() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!event.getItem().is(ModItems.HOLY_GOLD_KNIGHT_PIE
        )) {
            return;
        }

        if (player.getRandom().nextFloat() >= 0.1F) {
            return;
        }

        player.level().getEntitiesOfClass(Sheep.class, player.getBoundingBox().inflate(10.0D)).forEach(sheep -> {
            sheep.setTarget(player);
        });
    }

    @SubscribeEvent
    public void onSheepTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Sheep sheep)) {
            return;
        }

        if (sheep.level().isClientSide()) {
            return;
        }

        if (sheep.getTarget() instanceof Player player) {
            if (sheep.distanceTo(player) > 2.0F) {
                sheep.getNavigation().moveTo(player, 1.3D);
            }

            if (sheep.distanceTo(player) < 1.8F) {
                player.hurt(sheep.damageSources().mobAttack(sheep), 2.0F);
            }
        }
    }
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == ModItems.HOLY_GOLD_KNIGHT_PIE.get()) {
            event.getToolTip().add(Component.translatable("tooltip.draconagric.holy_gold_knights_pie.line1"));
            event.getToolTip().add(Component.translatable("tooltip.draconagric.holy_gold_knights_pie.line2"));
            event.getToolTip().add(Component.translatable("tooltip.draconagric.holy_gold_knights_pie.line3"));
        }
    }

}
