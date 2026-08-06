package net.aolkzyeoer.draconagric.block;

import net.aolkzyeoer.draconagric.item.ModItems;
import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;


public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS  =
            DeferredRegister.createBlocks(Draconagric.MOD_ID);
    //此处注册新方块 NEW BLOCKS
    public static final DeferredBlock<Block> EMBERBLUE_ORE =
            registerBlocks("emberblue_ore",() -> new Block(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 9.0F)));

    public static final DeferredBlock<EmberBlueFarmlandBlock> EMBERBLUE_FARMLAND =
            BLOCKS.register(
                    "emberblue_farmland",
                    () -> new EmberBlueFarmlandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND))
            );





    //在注册方块的同时::同时注册物品状态的方块 BLOCKS->ITEMS
    public static final DeferredBlock<ArcanvilTableBlock> ARCANVIL_TABLE =
            registerBlocks("arcanvil_table", () -> new ArcanvilTableBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 9.0F)));

    private static <T extends Block> void registerBlockItems(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> DeferredBlock<T> registerBlocks(String name, Supplier<T> block){
        DeferredBlock<T> blocks = BLOCKS.register(name, block);
        registerBlockItems(name, blocks);
        return blocks;
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
