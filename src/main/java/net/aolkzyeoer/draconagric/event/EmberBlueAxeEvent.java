package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = Draconagric.MOD_ID)
public class EmberBlueAxeEvent {
    private static final int MAX_LOGS = 512;
    private static final int MIN_TREE_LOGS = 2;
    private static final int MIN_TREE_LEAVES = 4;
    private static final int LEAF_RADIUS = 6;

    private static final Set<UUID> READY_PLAYERS = new HashSet<>();
    private static final Set<UUID> CHOPPING_PLAYERS = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        UUID uuid = player.getUUID();
        ItemStack stack = player.getMainHandItem();

        if (!player.isShiftKeyDown() || !stack.is(ModItems.EMBERBLUE_AXE.get())) {
            READY_PLAYERS.remove(uuid);
            return;
        }

        if (READY_PLAYERS.add(uuid)) {
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
    public static void onBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) {
            return;
        }

        UUID uuid = player.getUUID();
        if (CHOPPING_PLAYERS.contains(uuid)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.EMBERBLUE_AXE.get()) || !READY_PLAYERS.contains(uuid)) {
            return;
        }

        BlockPos start = event.getPos();
        Level level = player.level();
        BlockState startState = level.getBlockState(start);
        if (!startState.is(BlockTags.LOGS)) {
            READY_PLAYERS.remove(uuid);
            return;
        }

        Set<BlockPos> logs = collectConnectedLogs(level, start);
        if (!isTree(level, logs)) {
            READY_PLAYERS.remove(uuid);
            return;
        }

        READY_PLAYERS.remove(uuid);
        event.setCanceled(true);
        CHOPPING_PLAYERS.add(uuid);

        int durabilityCost = 0;
        try {
            durabilityCost += breakBlocks(level, player, stack, logs, true);

            Set<BlockPos> leaves = collectLeaves(level, logs);
            int leafBlocks = breakBlocks(level, player, stack, leaves, false);
            durabilityCost += Math.max(1, leafBlocks / 6);

            if (durabilityCost > 0) {
                stack.hurtAndBreak(
                        durabilityCost,
                        player,
                        player.getEquipmentSlotForItem(stack)
                );
            }
        } finally {
            CHOPPING_PLAYERS.remove(uuid);
        }
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !event.isCriticalHit()) {
            return;
        }

        if (!player.getMainHandItem().is(ModItems.EMBERBLUE_AXE.get())) {
            return;
        }

        if (event.getTarget() instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0), player);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 0), player);
        }
    }

    private static Set<BlockPos> collectConnectedLogs(Level level, BlockPos start) {
        Set<BlockPos> logs = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());

        while (!queue.isEmpty() && logs.size() < MAX_LOGS) {
            BlockPos pos = queue.removeFirst();
            if (!logs.add(pos)) {
                continue;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }

                        BlockPos next = pos.offset(dx, dy, dz).immutable();
                        if (!logs.contains(next) && level.getBlockState(next).is(BlockTags.LOGS)) {
                            queue.add(next);
                        }
                    }
                }
            }
        }

        return logs;
    }

    private static boolean isTree(Level level, Set<BlockPos> logs) {
        if (logs.size() < MIN_TREE_LOGS) {
            return false;
        }

        int maxY = logs.stream().mapToInt(BlockPos::getY).max().orElse(Integer.MIN_VALUE);
        int leafCount = 0;

        for (BlockPos log : logs) {
            if (log.getY() < maxY - 3) {
                continue;
            }

            for (BlockPos pos : BlockPos.betweenClosed(
                    log.getX() - 2,
                    log.getY() - 2,
                    log.getZ() - 2,
                    log.getX() + 2,
                    log.getY() + 2,
                    log.getZ() + 2
            )) {
                if (level.getBlockState(pos).is(BlockTags.LEAVES) && ++leafCount >= MIN_TREE_LEAVES) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Set<BlockPos> collectLeaves(Level level, Set<BlockPos> logs) {
        Set<BlockPos> leaves = new HashSet<>();
        if (logs.isEmpty()) {
            return leaves;
        }

        int minX = logs.stream().mapToInt(BlockPos::getX).min().orElse(0) - LEAF_RADIUS;
        int minY = logs.stream().mapToInt(BlockPos::getY).min().orElse(0);
        int minZ = logs.stream().mapToInt(BlockPos::getZ).min().orElse(0) - LEAF_RADIUS;
        int maxX = logs.stream().mapToInt(BlockPos::getX).max().orElse(0) + LEAF_RADIUS;
        int maxY = logs.stream().mapToInt(BlockPos::getY).max().orElse(0) + LEAF_RADIUS;
        int maxZ = logs.stream().mapToInt(BlockPos::getZ).max().orElse(0) + LEAF_RADIUS;

        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (level.getBlockState(pos).is(BlockTags.LEAVES)) {
                leaves.add(pos.immutable());
            }
        }

        return leaves;
    }

    private static int breakBlocks(Level level, Player player, ItemStack stack, Set<BlockPos> positions, boolean requireHarvest) {
        int broken = 0;

        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || state.is(Blocks.BEDROCK)) {
                continue;
            }

            if (requireHarvest && !state.canHarvestBlock(level, pos, player)) {
                continue;
            }

            Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, stack);
            level.destroyBlock(pos, false);
            broken++;
        }

        return broken;
    }
}
