package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.castmagic.CastMagicType;
import net.aolkzyeoer.draconagric.castmagic.CastMagicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = Draconagric.MOD_ID)
public final class CastMagicEvents {
    private static final Set<UUID> USED_DOUBLE_JUMP = new HashSet<>();
    private static final TextColor LIGHT_GREEN = TextColor.fromRgb(0x90EE90);

    private CastMagicEvents() {
    }

    public static void tryDoubleJump(ServerPlayer player) {
        if (player.onGround() || player.getAbilities().flying || USED_DOUBLE_JUMP.contains(player.getUUID())) {
            return;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!CastMagicUtil.hasType(boots, CastMagicType.DOUBLE_JUMP)) {
            return;
        }

        USED_DOUBLE_JUMP.add(player.getUUID());
        player.fallDistance = 0.0F;
        player.setDeltaMovement(player.getDeltaMovement().x, 0.55D, player.getDeltaMovement().z);
        player.hasImpulse = true;
        player.hurtMarked = true;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide && player.onGround()) {
            USED_DOUBLE_JUMP.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.getEntity().getMainHandItem();
        if (!CastMagicUtil.isTool(tool)) {
            return;
        }
        float bonus = 0.0F;
        if (CastMagicUtil.hasType(tool, CastMagicType.QUARTZ)) {
            bonus += 3.0F;
        }
        if (CastMagicUtil.hasType(tool, CastMagicType.DIAMOND)) {
            bonus += 5.0F;
        }
        if (CastMagicUtil.hasType(tool, CastMagicType.NETHERITE)) {
            bonus += 7.0F;
        }
        if (bonus > 0.0F) {
            event.setNewSpeed(event.getNewSpeed() + bonus);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        float multiplier = 1.0F;
        for (ItemStack armor : entity.getArmorSlots()) {
            multiplier *= CastMagicUtil.resistanceMultiplier(armor, event.getSource());
        }
        if (multiplier != 1.0F) {
            event.setAmount(event.getAmount() * multiplier);
        }

        if (!entity.level().isClientSide) {
            for (ItemStack armor : entity.getArmorSlots()) {
                for (MobEffectInstance effect : CastMagicUtil.getStoredPotionEffects(armor, 100)) {
                    entity.addEffect(effect);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (weapon.isEmpty() || attacker.level().isClientSide) {
            return;
        }
        for (MobEffectInstance effect : CastMagicUtil.getStoredPotionEffects(weapon, 60)) {
            event.getEntity().addEffect(effect, attacker);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker) || !(attacker.level() instanceof ServerLevel level)) {
            return;
        }
        ItemStack weapon = attacker.getMainHandItem();
        if (!CastMagicUtil.isWeapon(weapon) || !CastMagicUtil.hasType(weapon, CastMagicType.EMERALD)) {
            return;
        }
        duplicateDrops(event.getDrops(), level, 0.35F);
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        ItemStack tool = event.getTool();
        if (!CastMagicUtil.isTool(tool) || !CastMagicUtil.hasType(tool, CastMagicType.EMERALD)) {
            return;
        }
        duplicateDrops(event.getDrops(), event.getLevel(), 0.45F);
    }

    @SubscribeEvent
    public static void onItemInvulnerability(EntityInvulnerabilityCheckEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) {
            return;
        }
        if (!event.getSource().is(DamageTypeTags.IS_FIRE)) {
            return;
        }
        ItemStack stack = itemEntity.getItem();
        if (CastMagicUtil.hasType(stack, CastMagicType.NETHERITE)) {
            event.setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<CastMagicType> types = CastMagicUtil.getTypes(stack);
        if (types.isEmpty()) {
            return;
        }
        List<Component> lines = event.getToolTip();
        for (CastMagicType type : types) {
            if (type == CastMagicType.POTION) {
                addPotionLines(lines, stack);
            } else if (type == CastMagicType.DOUBLE_JUMP) {
                lines.add(Component.literal("[")
                        .append(Component.translatable("castmagic.draconagric.double_jump"))
                        .append("]")
                        .withStyle(Style.EMPTY.withColor(LIGHT_GREEN)));
            } else {
                lines.add(mineralLine(type));
            }
        }
    }

    private static void duplicateDrops(Collection<ItemEntity> drops, ServerLevel level, float chance) {
        List<ItemEntity> added = new ArrayList<>();
        for (ItemEntity drop : drops) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty() || level.random.nextFloat() > chance) {
                continue;
            }
            ItemStack copy = stack.copy();
            copy.setCount(Math.max(1, stack.getCount()));
            added.add(new ItemEntity(level, drop.getX(), drop.getY(), drop.getZ(), copy));
        }
        drops.addAll(added);
    }

    private static Component mineralLine(CastMagicType type) {
        return switch (type) {
            case LAPIS -> Component.literal("[")
                    .append(Component.translatable("castmagic.draconagric.lapis"))
                    .append("]")
                    .withStyle(ChatFormatting.BLUE);
            case QUARTZ -> Component.literal("[")
                    .append(Component.translatable("castmagic.draconagric.quartz"))
                    .append("]")
                    .withStyle(ChatFormatting.WHITE);
            case EMERALD -> Component.literal("[")
                    .append(Component.translatable("castmagic.draconagric.emerald"))
                    .append("]")
                    .withStyle(ChatFormatting.GREEN);
            case DIAMOND -> Component.literal("[")
                    .append(Component.translatable("castmagic.draconagric.diamond"))
                    .append("]")
                    .withStyle(ChatFormatting.AQUA);
            case NETHERITE -> Component.literal("[")
                    .append(Component.translatable("castmagic.draconagric.netherite"))
                    .append("]")
                    .withStyle(ChatFormatting.DARK_GRAY);
            default -> Component.empty();
        };
    }

    private static void addPotionLines(List<Component> lines, ItemStack stack) {
        for (MobEffectInstance effect : CastMagicUtil.getStoredPotionEffects(stack, 1)) {
            MutableComponent line = Component.literal("[")
                    .append(effect.getEffect().value().getDisplayName())
                    .append(" " + roman(effect.getAmplifier() + 1))
                    .append("]");
            if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                lines.add(line.withStyle(ChatFormatting.RED));
            } else {
                lines.add(line.withStyle(Style.EMPTY.withColor(LIGHT_GREEN)));
            }
        }
    }

    private static String roman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> Integer.toString(level);
        };
    }
}
