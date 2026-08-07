package net.aolkzyeoer.draconagric.item;

import net.aolkzyeoer.draconagric.block.ModBlocks;
import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Supplier;


public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Draconagric.MOD_ID);

    public static final Supplier<CreativeModeTab> LDSDELIGHT_TAB =
            CREATIVE_MODE_TABS.register("draconagric_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.HOLY_GOLD_KNIGHT_PIE.get()))
                    .title(Component.translatable("itemGroup.draconagric_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.DRAGON_HORN);
                        output.accept(ModItems.EMBERBLUE);
                        //以后添加食物栏后可接受:
                        output.accept(ModItems.ANCIENT_DRAGON_EGG_LIQUID);//
                        output.accept(ModItems.DRAGON_EGG_SUSHI);//
                        output.accept(ModItems.SLICE_OF_DRAGON_EGG_CAKE);//
                        output.accept(ModItems.FRIED_DRAGON_EGG_BURGER);//
                        output.accept(ModItems.DRAGON_EGG_SANDWICH);//
                        output.accept(ModItems.DRAGON_EGG_RICE_BOWL);//
                        output.accept(ModItems.SHIT);//
                        output.accept(ModItems.MANLANBA);//
                        //方块:
                        output.accept(ModBlocks.EMBERBLUE_ORE);//
                        //贡献者物品
                        output.accept(ModItems.HOLY_GOLD_KNIGHT_PIE); //贡献:定义为唐
                        output.accept(ModItems.LAPIS_FRIED_RICE); //贡献:秋山澪
                        //工具:
                        output.accept(ModItems.EMBERBLUE_PICKHAMMER);
                        output.accept(ModItems.EMBERBLUE_AXE);
                        output.accept(ModItems.EMBERBLUE_SWORD);
                        output.accept(ModItems.EMBERBLUE_HOE);
                        output.accept(ModItems.DOUBLE_JUMP_SCROLL);
                        output.accept(ModItems.DISARM_SCROLL);
                        output.accept(ModItems.ARCANE_CATALYST);
                        output.accept(ModBlocks.ARCANVIL_TABLE);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}



