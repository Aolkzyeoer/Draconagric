package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = Draconagric.MOD_ID)
public class EmberBlueSwordEvent {
    private static final Set<UUID> READY_PLAYERS = new HashSet<>();
    private static final Set<UUID> CHARGED_ATTACK = new HashSet<>();
    private static final Set<UUID> CHARGED_DEFENSE = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        UUID uuid = player.getUUID();
        ItemStack stack = player.getMainHandItem();

        if (!player.isShiftKeyDown() || !stack.is(ModItems.EMBERBLUE_SWORD.get())) {
            READY_PLAYERS.remove(uuid);
            return;
        }

        if (READY_PLAYERS.add(uuid)) {
            CHARGED_ATTACK.add(uuid);
            CHARGED_DEFENSE.add(uuid);
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

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Player defender
                && CHARGED_DEFENSE.remove(defender.getUUID())) {
            event.setNewDamage(event.getNewDamage() * 0.5F);
        }

        if (event.getSource().getEntity() instanceof Player attacker
                && attacker.getMainHandItem().is(ModItems.EMBERBLUE_SWORD.get())
                && CHARGED_ATTACK.remove(attacker.getUUID())) {
            event.setNewDamage(event.getNewDamage() * 6.0F);
        }
    }
}
