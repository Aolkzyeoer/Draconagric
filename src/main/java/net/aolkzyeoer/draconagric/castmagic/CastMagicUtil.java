package net.aolkzyeoer.draconagric.castmagic;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class CastMagicUtil {
    public static final int EXPERIENCE_LEVEL_COST = 5;
    private static final String ROOT = "draconagric_castmagic";
    private static final String TYPES = "types";
    private static final String POTION_EFFECTS = "potion_effects";

    private CastMagicUtil() {
    }

    public static boolean canCast(ItemStack equipment, ItemStack material) {
        if (equipment.isEmpty() || material.isEmpty() || !isCastableEquipment(equipment)) {
            return false;
        }
        Optional<CastMagicType> type = typeForMaterial(material);
        return type.isPresent() && canApplyType(equipment, type.get(), material);
    }

    public static boolean canApplyType(ItemStack equipment, CastMagicType type, ItemStack material) {
        if (hasType(equipment, type)) {
            return false;
        }
        return switch (type) {
            case LAPIS -> equipment.has(DataComponents.MAX_DAMAGE);
            case QUARTZ, EMERALD, DIAMOND, NETHERITE, POTION -> isWeapon(equipment) || isTool(equipment) || isArmor(equipment);
            case DOUBLE_JUMP -> isBoots(equipment);
            case DISARM -> isWeapon(equipment) || isTool(equipment);
        };
    }

    public static Optional<CastMagicType> typeForMaterial(ItemStack material) {
        if (material.is(Items.LAPIS_LAZULI)) {
            return Optional.of(CastMagicType.LAPIS);
        }
        if (material.is(Items.QUARTZ)) {
            return Optional.of(CastMagicType.QUARTZ);
        }
        if (material.is(Items.EMERALD)) {
            return Optional.of(CastMagicType.EMERALD);
        }
        if (material.is(Items.DIAMOND)) {
            return Optional.of(CastMagicType.DIAMOND);
        }
        if (material.is(Items.NETHERITE_INGOT)) {
            return Optional.of(CastMagicType.NETHERITE);
        }
        if (isPotion(material)) {
            return Optional.of(CastMagicType.POTION);
        }
        if (material.is(ModItems.DOUBLE_JUMP_SCROLL.get())) {
            return Optional.of(CastMagicType.DOUBLE_JUMP);
        }
        if (material.is(ModItems.DISARM_SCROLL.get())) {
            return Optional.of(CastMagicType.DISARM);
        }
        return Optional.empty();
    }

    public static boolean isSmithingTemplate(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getPath().endsWith("_smithing_template");
    }

    public static boolean isPotion(ItemStack stack) {
        return stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION)
                || stack.is(Items.LINGERING_POTION);
    }

    public static boolean isScroll(ItemStack stack) {
        return stack.is(ModItems.DOUBLE_JUMP_SCROLL.get())
                || stack.is(ModItems.DISARM_SCROLL.get());
    }

    public static boolean isCastableEquipment(ItemStack stack) {
        return isWeapon(stack) || isTool(stack) || isArmor(stack);
    }

    public static boolean isCastMaterial(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.LAPIS_LAZULI)
                || stack.is(Items.QUARTZ)
                || stack.is(Items.EMERALD)
                || stack.is(Items.DIAMOND)
                || stack.is(Items.NETHERITE_INGOT)
                || stack.is(Items.COPPER_INGOT)
                || stack.is(Items.GOLD_INGOT)
                || stack.is(Items.REDSTONE)
                || stack.is(Items.AMETHYST_SHARD)
                || stack.is(Items.ENDER_PEARL)
                || stack.is(ModItems.EMBERBLUE.get());
    }

    public static CastResult applyCast(Player player, ItemStack equipment, ItemStack material) {
        Optional<CastMagicType> optionalType = typeForMaterial(material);
        if (optionalType.isEmpty()) {
            return CastResult.INVALID_MATERIAL;
        }

        CastMagicType type = optionalType.get();
        if (!canApplyType(equipment, type, material)) {
            return CastResult.INVALID_EQUIPMENT;
        }

        CompoundTag root = mutableRoot(equipment);
        addType(root, type);

        if (type == CastMagicType.LAPIS) {
            Integer maxDamage = equipment.get(DataComponents.MAX_DAMAGE);
            if (maxDamage != null) {
                equipment.set(DataComponents.MAX_DAMAGE, maxDamage + 200);
            }
        }

        if (type == CastMagicType.POTION) {
            writePotionEffects(root, material);
        }

        equipment.set(DataComponents.CUSTOM_DATA, CustomData.of(withRoot(equipment, root)));
        rebuildAttributes(equipment);
        return CastResult.SUCCESS;
    }

    public static boolean hasType(ItemStack stack, CastMagicType type) {
        CompoundTag root = readRoot(stack);
        if (root == null) {
            return false;
        }
        ListTag types = root.getList(TYPES, Tag.TAG_STRING);
        String key = type.name().toLowerCase(Locale.ROOT);
        for (int i = 0; i < types.size(); i++) {
            if (types.getString(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static List<CastMagicType> getTypes(ItemStack stack) {
        CompoundTag root = readRoot(stack);
        List<CastMagicType> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        ListTag types = root.getList(TYPES, Tag.TAG_STRING);
        for (int i = 0; i < types.size(); i++) {
            try {
                result.add(CastMagicType.valueOf(types.getString(i).toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return result;
    }

    public static List<MobEffectInstance> getStoredPotionEffects(ItemStack stack, int durationTicks) {
        CompoundTag root = readRoot(stack);
        List<MobEffectInstance> effects = new ArrayList<>();
        if (root == null || !root.contains(POTION_EFFECTS)) {
            return effects;
        }

        ListTag entries = root.getList(POTION_EFFECTS, Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(entry.getString("id"));
            if (id == null) {
                continue;
            }
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
            if (effect == null) {
                continue;
            }
            int amplifier = entry.getInt("amplifier");
            effects.add(new MobEffectInstance(Holder.direct(effect), durationTicks, amplifier));
        }
        return effects;
    }

    public static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    public static boolean isTool(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof DiggerItem || item instanceof TieredItem && !(item instanceof SwordItem);
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public static boolean isBoots(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.FEET;
    }

    public static float resistanceMultiplier(ItemStack armor, DamageSource source) {
        float multiplier = 1.0F;
        if (hasType(armor, CastMagicType.EMERALD) && source.is(DamageTypeTags.IS_PROJECTILE)) {
            multiplier *= 0.85F;
        }
        if (hasType(armor, CastMagicType.DIAMOND) && source.is(DamageTypeTags.IS_EXPLOSION)) {
            multiplier *= 0.85F;
        }
        if (hasType(armor, CastMagicType.NETHERITE) && source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            multiplier *= 0.80F;
        }
        return multiplier;
    }

    private static void addType(CompoundTag root, CastMagicType type) {
        ListTag types = root.getList(TYPES, Tag.TAG_STRING);
        String key = type.name().toLowerCase(Locale.ROOT);
        for (int i = 0; i < types.size(); i++) {
            if (types.getString(i).equals(key)) {
                return;
            }
        }
        types.add(net.minecraft.nbt.StringTag.valueOf(key));
        root.put(TYPES, types);
    }

    private static void writePotionEffects(CompoundTag root, ItemStack potionStack) {
        PotionContents contents = potionStack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return;
        }
        ListTag effects = new ListTag();
        for (MobEffectInstance effect : contents.getAllEffects()) {
            ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value());
            if (effectId == null) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putString("id", effectId.toString());
            entry.putInt("amplifier", effect.getAmplifier());
            effects.add(entry);
        }
        root.put(POTION_EFFECTS, effects);
    }

    private static void rebuildAttributes(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

        if (isWeapon(stack)) {
            if (hasType(stack, CastMagicType.QUARTZ)) {
                modifiers = modifiers.withModifierAdded(
                        Attributes.ATTACK_DAMAGE,
                        modifier("quartz_weapon_damage", 1.0D),
                        EquipmentSlotGroup.MAINHAND
                );
            }
            if (hasType(stack, CastMagicType.DIAMOND)) {
                modifiers = modifiers.withModifierAdded(
                        Attributes.ATTACK_SPEED,
                        modifier("diamond_weapon_speed", 0.2D),
                        EquipmentSlotGroup.MAINHAND
                );
            }
            if (hasType(stack, CastMagicType.NETHERITE)) {
                modifiers = modifiers.withModifierAdded(
                        Attributes.ATTACK_DAMAGE,
                        modifier("netherite_weapon_damage", 2.0D),
                        EquipmentSlotGroup.MAINHAND
                );
            }
        }

        if (isArmor(stack) && stack.getItem() instanceof ArmorItem armorItem) {
            EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(armorItem.getEquipmentSlot());
            if (hasType(stack, CastMagicType.QUARTZ)) {
                modifiers = modifiers.withModifierAdded(
                        Attributes.ARMOR_TOUGHNESS,
                        modifier("quartz_armor_toughness", 1.0D),
                        group
                );
            }
            if (hasType(stack, CastMagicType.NETHERITE)) {
                modifiers = modifiers.withModifierAdded(
                        Attributes.KNOCKBACK_RESISTANCE,
                        modifier("netherite_knockback_resistance", 0.10D),
                        group
                );
            }
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
    }

    private static AttributeModifier modifier(String name, double amount) {
        return new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "castmagic_" + name),
                amount,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    private static CompoundTag readRoot(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        CompoundTag tag = customData.copyTag();
        return tag.contains(ROOT) ? tag.getCompound(ROOT) : null;
    }

    private static CompoundTag mutableRoot(ItemStack stack) {
        CompoundTag root = readRoot(stack);
        return root == null ? new CompoundTag() : root;
    }

    private static CompoundTag withRoot(ItemStack stack, CompoundTag root) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData == null ? new CompoundTag() : customData.copyTag();
        tag.put(ROOT, root);
        return tag;
    }

    public enum CastResult {
        SUCCESS,
        INVALID_EQUIPMENT,
        INVALID_MATERIAL
    }
}
