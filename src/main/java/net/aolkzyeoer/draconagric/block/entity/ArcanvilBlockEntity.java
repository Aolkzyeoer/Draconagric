package net.aolkzyeoer.draconagric.block.entity;

import net.aolkzyeoer.draconagric.castmagic.CastMagicUtil;
import net.aolkzyeoer.draconagric.menu.ArcanvilMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ArcanvilBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int POTION_SLOT = 0;
    public static final int SCROLL_SLOT = 1;
    public static final int EQUIPMENT_SLOT = 2;
    public static final int TEMPLATE_SLOT = 3;
    public static final int MINERAL_SLOT = 4;
    public static final int FIRST_STORAGE_SLOT = 5;
    public static final int STORAGE_POTION_START = 5;
    public static final int STORAGE_MINERAL_START = 7;
    public static final int STORAGE_TEMPLATE_START = 9;
    public static final int STORAGE_SCROLL_START = 11;
    public static final int CONTAINER_SIZE = 13;
    public static final int MAX_PROGRESS = 100;

    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private int progress;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? ArcanvilBlockEntity.this.progress : MAX_PROGRESS;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                ArcanvilBlockEntity.this.progress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ArcanvilBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ARCANVIL_TABLE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcanvilBlockEntity blockEntity) {
        if (blockEntity.progress <= 0) {
            return;
        }

        blockEntity.progress++;
        if (blockEntity.progress >= MAX_PROGRESS) {
            blockEntity.finishCast();
            blockEntity.progress = 0;
        }
        setChanged(level, pos, state);
    }

    public boolean startCast(ServerPlayer player, ItemStack catalyst) {
        if (progress > 0) {
            player.displayClientMessage(Component.translatable("message.draconagric.arcanvil_table.busy"), true);
            return false;
        }
        if (!canCast(player)) {
            player.displayClientMessage(Component.translatable("message.draconagric.arcanvil_table.invalid"), true);
            return false;
        }

        player.giveExperienceLevels(-CastMagicUtil.EXPERIENCE_LEVEL_COST);
        catalyst.hurtAndBreak(1, player, player.getEquipmentSlotForItem(catalyst));
        progress = 1;
        setChanged();
        return true;
    }

    public boolean canCast(Player player) {
        if (player.experienceLevel < CastMagicUtil.EXPERIENCE_LEVEL_COST) {
            return false;
        }
        ItemStack equipment = getItem(EQUIPMENT_SLOT);
        ItemStack material = findMaterial();
        ItemStack template = findTemplate();
        return !template.isEmpty() && CastMagicUtil.canCast(equipment, material);
    }

    public boolean isCasting() {
        return progress > 0;
    }

    public int getProgress() {
        return progress;
    }

    public ContainerData getData() {
        return data;
    }

    private void finishCast() {
        ItemStack equipment = getItem(EQUIPMENT_SLOT);
        int materialSlot = findMaterialSlot();
        int templateSlot = findTemplateSlot();
        if (materialSlot < 0 || templateSlot < 0) {
            return;
        }

        CastMagicUtil.CastResult result = CastMagicUtil.applyCast(null, equipment, getItem(materialSlot));
        if (result != CastMagicUtil.CastResult.SUCCESS) {
            return;
        }
        getItem(materialSlot).shrink(1);
        getItem(templateSlot).shrink(1);
        setChanged();
    }

    private ItemStack findTemplate() {
        int slot = findTemplateSlot();
        return slot < 0 ? ItemStack.EMPTY : getItem(slot);
    }

    private int findTemplateSlot() {
        if (CastMagicUtil.isSmithingTemplate(getItem(TEMPLATE_SLOT))) {
            return TEMPLATE_SLOT;
        }
        for (int slot = STORAGE_TEMPLATE_START; slot < STORAGE_SCROLL_START; slot++) {
            if (CastMagicUtil.isSmithingTemplate(getItem(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private ItemStack findMaterial() {
        int slot = findMaterialSlot();
        return slot < 0 ? ItemStack.EMPTY : getItem(slot);
    }

    private int findMaterialSlot() {
        if (CastMagicUtil.typeForMaterial(getItem(SCROLL_SLOT)).isPresent()) {
            return SCROLL_SLOT;
        }
        if (CastMagicUtil.typeForMaterial(getItem(POTION_SLOT)).isPresent()) {
            return POTION_SLOT;
        }
        if (CastMagicUtil.typeForMaterial(getItem(MINERAL_SLOT)).isPresent()) {
            return MINERAL_SLOT;
        }
        for (int slot = STORAGE_POTION_START; slot < STORAGE_TEMPLATE_START; slot++) {
            if (CastMagicUtil.typeForMaterial(getItem(slot)).isPresent()) {
                return slot;
            }
        }
        for (int slot = STORAGE_SCROLL_START; slot < CONTAINER_SIZE; slot++) {
            if (CastMagicUtil.typeForMaterial(getItem(slot)).isPresent()) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.draconagric.arcanvil_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ArcanvilMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
        progress = tag.getInt("Progress");
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (isCasting()) {
            return false;
        }
        if (slot == POTION_SLOT) {
            return CastMagicUtil.isPotion(stack);
        }
        if (slot == SCROLL_SLOT) {
            return CastMagicUtil.isScroll(stack);
        }
        if (slot == EQUIPMENT_SLOT) {
            return CastMagicUtil.isCastableEquipment(stack);
        }
        if (slot == TEMPLATE_SLOT) {
            return CastMagicUtil.isSmithingTemplate(stack);
        }
        if (slot == MINERAL_SLOT) {
            return CastMagicUtil.isCastMaterial(stack);
        }
        if (slot >= STORAGE_POTION_START && slot < STORAGE_MINERAL_START) {
            return CastMagicUtil.isPotion(stack);
        }
        if (slot >= STORAGE_MINERAL_START && slot < STORAGE_TEMPLATE_START) {
            return CastMagicUtil.isCastMaterial(stack);
        }
        if (slot >= STORAGE_TEMPLATE_START && slot < STORAGE_SCROLL_START) {
            return CastMagicUtil.isSmithingTemplate(stack);
        }
        return slot >= STORAGE_SCROLL_START && CastMagicUtil.isScroll(stack);
    }

    @Override
    public void clearContent() {
        items.clear();
    }
}
