package net.aolkzyeoer.draconagric.client.castmagic;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.castmagic.CastMagicType;
import net.aolkzyeoer.draconagric.castmagic.CastMagicUtil;
import net.aolkzyeoer.draconagric.network.DoubleJumpPayload;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Draconagric.MOD_ID, value = Dist.CLIENT)
public final class DoubleJumpClientEvents {
    private static boolean wasJumping;
    private static boolean usedDoubleJump;

    private DoubleJumpClientEvents() {
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (event.getEntity().onGround()) {
            usedDoubleJump = false;
        }

        boolean jumping = event.getInput().jumping;
        if (jumping && !wasJumping && !event.getEntity().onGround() && !event.getEntity().getAbilities().flying && !usedDoubleJump) {
            ItemStack boots = event.getEntity().getItemBySlot(EquipmentSlot.FEET);
            if (CastMagicUtil.hasType(boots, CastMagicType.DOUBLE_JUMP)) {
                usedDoubleJump = true;
                PacketDistributor.sendToServer(new DoubleJumpPayload());
            }
        }
        wasJumping = jumping;
    }
}
