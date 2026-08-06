package net.aolkzyeoer.draconagric.menu;

import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, Draconagric.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ArcanvilMenu>> ARCANVIL_MENU =
            MENU_TYPES.register("arcanvil_table", () -> IMenuTypeExtension.create(ArcanvilMenu::new));

    private ModMenuTypes() {
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
