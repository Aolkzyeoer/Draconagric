package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = Draconagric.MOD_ID)
public class EmberBlueHoeEvent {
    private static final Set<UUID> CHARGING_PLAYERS = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        UUID uuid = player.getUUID();
        ItemStack stack = player.getMainHandItem();
        if (!player.isShiftKeyDown() || !stack.is(ModItems.EMBERBLUE_HOE.get())) {
            CHARGING_PLAYERS.remove(uuid);
            return;
        }

        if (CHARGING_PLAYERS.add(uuid)) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
    }
}
