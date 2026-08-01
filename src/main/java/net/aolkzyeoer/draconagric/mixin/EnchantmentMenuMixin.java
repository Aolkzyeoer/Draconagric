package net.aolkzyeoer.draconagric.mixin;

import net.aolkzyeoer.draconagric.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
    @Unique
    private static final int DRACONAGRIC_LAPIS_MIN_LEVEL = 30;

    @Unique
    private static final List<ResourceKey<Enchantment>> DRACONAGRIC_SPECIAL_ENCHANTMENTS = List.of(
            ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace("mending")),
            ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace("swift_sneak")),
            ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace("soul_speed"))
    );

    @Unique
    private Player draconagric$player;

    @Unique
    private int draconagric$experienceLevelBeforeEnchant = -1;

    @Shadow
    @Final
    private RandomSource random;

    @Shadow
    @Final
    private net.minecraft.world.inventory.DataSlot enchantmentSeed;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void draconagric$capturePlayer(int containerId, Inventory playerInventory, ContainerLevelAccess access, CallbackInfo ci) {
        this.draconagric$player = playerInventory.player;
    }

    @Inject(method = "getEnchantmentList", at = @At("HEAD"), cancellable = true)
    private void draconagric$boostEnchantmentList(
            RegistryAccess registryAccess,
            ItemStack stack,
            int slot,
            int cost,
            CallbackInfoReturnable<List<EnchantmentInstance>> cir
    ) {
        if (!draconagric$hasLapisEnchantment()) {
            return;
        }

        this.random.setSeed((long) (this.enchantmentSeed.get() + slot));
        Registry<Enchantment> registry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);
        Optional<HolderSet.Named<Enchantment>> tableEnchantments = registry.getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (tableEnchantments.isEmpty()) {
            cir.setReturnValue(List.of());
            return;
        }

        int effectiveCost = cost;
        if (this.draconagric$player.experienceLevel < DRACONAGRIC_LAPIS_MIN_LEVEL) {
            effectiveCost = Math.max(effectiveCost, DRACONAGRIC_LAPIS_MIN_LEVEL);
        }

        Stream<Holder<Enchantment>> candidates = Stream.concat(
                tableEnchantments.get().stream(),
                draconagric$specialEnchantments(registry)
        ).distinct();

        List<EnchantmentInstance> enchantments = EnchantmentHelper.selectEnchantment(
                this.random,
                stack,
                effectiveCost,
                candidates
        );

        if (stack.is(Items.BOOK) && enchantments.size() > 1) {
            enchantments.remove(this.random.nextInt(enchantments.size()));
        }

        cir.setReturnValue(enchantments);
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    private void draconagric$captureExperienceLevel(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        this.draconagric$experienceLevelBeforeEnchant = player.experienceLevel;
    }

    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    private void draconagric$refundDiscountedExperience(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        int levelBeforeEnchant = this.draconagric$experienceLevelBeforeEnchant;
        this.draconagric$experienceLevelBeforeEnchant = -1;

        if (cir.getReturnValueZ()
                && draconagric$hasLapisEnchantment()
                && levelBeforeEnchant > DRACONAGRIC_LAPIS_MIN_LEVEL) {
            int paidLevels = levelBeforeEnchant - player.experienceLevel;
            int refund = paidLevels / 2;
            if (refund > 0) {
                player.giveExperienceLevels(refund);
            }
        }
    }

    @Unique
    private boolean draconagric$hasLapisEnchantment() {
        return this.draconagric$player != null && this.draconagric$player.hasEffect(ModEffects.LAPIS_ENCHANTMENT);
    }

    @Unique
    private static Stream<Holder<Enchantment>> draconagric$specialEnchantments(Registry<Enchantment> registry) {
        return DRACONAGRIC_SPECIAL_ENCHANTMENTS.stream()
                .map(registry::getHolder)
                .flatMap(Optional::stream);
    }
}
