package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = Draconagric.MOD_ID)
public class EmberBlueOreGenerationEvent {
    private static final int OUTER_ISLAND_DISTANCE_SQUARED = 1024 * 1024;
    private static final int VEINS_PER_CHUNK = 2;
    private static final int VEIN_SIZE = 4;
    private static final int MIN_Y = 8;
    private static final int MAX_Y = 96;

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.END) {
            return;
        }

        ChunkAccess chunk = event.getChunk();
        ChunkPos chunkPos = chunk.getPos();
        long centerX = chunkPos.getMinBlockX() + 8L;
        long centerZ = chunkPos.getMinBlockZ() + 8L;
        if (centerX * centerX + centerZ * centerZ < OUTER_ISLAND_DISTANCE_SQUARED) {
            return;
        }

        RandomSource random = RandomSource.create(level.getSeed() ^ chunkPos.toLong() ^ 0x4D2B1F3A9E3779B9L);
        for (int vein = 0; vein < VEINS_PER_CHUNK; vein++) {
            BlockPos origin = new BlockPos(
                    chunkPos.getMinBlockX() + random.nextInt(16),
                    MIN_Y + random.nextInt(MAX_Y - MIN_Y + 1),
                    chunkPos.getMinBlockZ() + random.nextInt(16)
            );
            placeVein(chunk, origin, random);
        }
    }

    private static void placeVein(ChunkAccess chunk, BlockPos origin, RandomSource random) {
        BlockPos.MutableBlockPos mutable = origin.mutable();
        int placed = 0;

        for (int attempt = 0; attempt < VEIN_SIZE * 4 && placed < VEIN_SIZE; attempt++) {
            mutable.setWithOffset(
                    origin,
                    random.nextInt(5) - 2,
                    random.nextInt(5) - 2,
                    random.nextInt(5) - 2
            );

            if (mutable.getY() < MIN_Y || mutable.getY() > MAX_Y) {
                continue;
            }

            if (chunk.getBlockState(mutable).is(Blocks.END_STONE)) {
                chunk.setBlockState(mutable, ModBlocks.EMBERBLUE_ORE.get().defaultBlockState(), false);
                placed++;
            }
        }
    }
}
