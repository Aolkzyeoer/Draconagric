package net.aolkzyeoer.draconagric.item;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.item.tier.EmberBlueTier;
import net.aolkzyeoer.draconagric.item.tool.EmberBlueAxe;
import net.aolkzyeoer.draconagric.item.tool.EmberBluePickhammer;
import net.aolkzyeoer.draconagric.item.tool.EmberBlueSword;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Draconagric.MOD_ID);

    public static final DeferredItem<Item> DRAGON_HORN =
            ITEMS.register("dragon_horn",() -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> EMBERBLUE =
            ITEMS.register("emberblue",() -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FRIED_DRAGON_EGG_BURGER =
            ITEMS.register("fried_dragon_egg_burger",() -> new Item(new Item.Properties().food(ModFoods.FRIED_DRAGON_EGG_BURGER)));
    public static final DeferredItem<Item> ANCIENT_DRAGON_EGG_LIQUID =
            ITEMS.register("ancient_dragon_egg_liquid",() -> new Item(new Item.Properties().food(ModFoods.ANCIENT_DRAGON_EGG_LIQUID)));
    public static final DeferredItem<Item> DRAGON_EGG_SUSHI =
            ITEMS.register("dragon_egg_sushi",() -> new Item(new Item.Properties().food(ModFoods.DRAGON_EGG_SUSHI)));
    public static final DeferredItem<Item> SLICE_OF_DRAGON_EGG_CAKE =
            ITEMS.register("slice_of_dragon_egg_cake",() -> new Item(new Item.Properties().food(ModFoods.SLICE_OF_DRAGON_EGG_CAKE)));
    public static final DeferredItem<Item> DRAGON_EGG_SANDWICH =
            ITEMS.register("dragon_egg_sandwich",() -> new Item(new Item.Properties().food(ModFoods.DRAGON_EGG_SANDWICH)));
    public static final DeferredItem<Item> DRAGON_EGG_RICE_BOWL =
            ITEMS.register("dragon_egg_rice_bowl",() -> new ModFoodItem(new Item.Properties().stacksTo(1),
                    ModFoods.DRAGON_EGG_RICE_BOWL));
    public static final DeferredItem<Item> HOLY_GOLD_KNIGHT_PIE =
            ITEMS.register("holy_gold_knight_pie",() -> new ModFoodItem(new Item.Properties().stacksTo(1),
                    ModFoods.HOLY_GOLD_KNIGHT_PIE));
    public static final DeferredItem<Item> LAPIS_FRIED_RICE =
            ITEMS.register("lapis_fried_rice",() -> new ModFoodItem(new Item.Properties().stacksTo(1),
                    ModFoods.LAPIS_FRIED_RICE));
    public static final DeferredItem<Item> SHIT =
            ITEMS.register("shit",() -> new Item(new Item.Properties().food(ModFoods.SHIT)));
    public static final DeferredItem<Item> MANLANBA =
            ITEMS.register("manlanba",() -> new GlintFoodItem(new Item.Properties().food(ModFoods.MANLANBA)));

    public static final DeferredItem<Item> EMBERBLUE_PICKHAMMER =
            ITEMS.register("emberblue_pickhammer",
                    () -> new EmberBluePickhammer(
                            EmberBlueTier.EMBERBLUE,
                            new Item.Properties().stacksTo(1).durability(4096)
                    )
            );

    public static final DeferredItem<Item> EMBERBLUE_AXE =
            ITEMS.register("emberblue_axe",
                    () -> new EmberBlueAxe(
                            EmberBlueTier.EMBERBLUE,
                            new Item.Properties().stacksTo(1).durability(6144)
                    )
            );

    public static final DeferredItem<Item> EMBERBLUE_SWORD =
            ITEMS.register("emberblue_sword",
                    () -> new EmberBlueSword(
                            EmberBlueTier.EMBERBLUE,
                            new Item.Properties().stacksTo(1).durability(4096)
                    )
            );




    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }
    }

