package net.aolkzyeoer.draconagric.effect;



import net.aolkzyeoer.draconagric.effect.ModEffects;
import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Draconagric.MOD_ID);

    public static final DeferredHolder<MobEffect, KnightsGloryEffect> KNIGHTS_GLORY =
            MOB_EFFECTS.register("knights_glory", () -> new KnightsGloryEffect());

    public static final DeferredHolder<MobEffect, LapisEnchantmentEffect> LAPIS_ENCHANTMENT =
            MOB_EFFECTS.register("lapis_enchantment", () -> new LapisEnchantmentEffect());

    public static void register(IEventBus eventBus) {
        ModEffects.register(eventBus);
    }


}
