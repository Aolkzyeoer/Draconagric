package net.aolkzyeoer.draconagric.menu;

import net.aolkzyeoer.draconagric.block.ModBlocks;
import net.aolkzyeoer.draconagric.block.entity.ArcanvilBlockEntity;
import net.aolkzyeoer.draconagric.castmagic.CastMagicUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArcanvilMenu extends AbstractContainerMenu {
    private static final int ARCANVIL_SLOT_COUNT = ArcanvilBlockEntity.CONTAINER_SIZE;

    private final Container container;
    private final ContainerData data;
    private final BlockPos pos;

    public ArcanvilMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, inventory, buffer.readBlockPos());
    }

    public ArcanvilMenu(int containerId, Inventory inventory, ArcanvilBlockEntity blockEntity, ContainerData data) {
        this(containerId, inventory, blockEntity, data, blockEntity.getBlockPos());
    }

    private ArcanvilMenu(int containerId, Inventory inventory, BlockPos pos) {
        this(containerId, inventory, containerAt(inventory, pos), dataAt(inventory, pos), pos);
    }

    private ArcanvilMenu(int containerId, Inventory inventory, Container container, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.ARCANVIL_MENU.get(), containerId);
        checkContainerSize(container, ARCANVIL_SLOT_COUNT);
        this.container = container;
        this.data = data;
        this.pos = pos;
        container.startOpen(inventory.player);

        addSlot(new ArcanvilSlot(container, ArcanvilBlockEntity.POTION_SLOT, 66, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !isCasting() && CastMagicUtil.isPotion(stack);
            }
        });
        addSlot(new ArcanvilSlot(container, ArcanvilBlockEntity.SCROLL_SLOT, 154, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !isCasting() && CastMagicUtil.isScroll(stack);
            }
        });
        addSlot(new ArcanvilSlot(container, ArcanvilBlockEntity.EQUIPMENT_SLOT, 110, 46) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !isCasting() && CastMagicUtil.isCastableEquipment(stack);
            }
        });
        addSlot(new ArcanvilSlot(container, ArcanvilBlockEntity.TEMPLATE_SLOT, 154, 74) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !isCasting() && CastMagicUtil.isSmithingTemplate(stack);
            }
        });
        addSlot(new ArcanvilSlot(container, ArcanvilBlockEntity.MINERAL_SLOT, 66, 74) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !isCasting() && CastMagicUtil.isCastMaterial(stack);
            }
        });

        int slot = ArcanvilBlockEntity.FIRST_STORAGE_SLOT;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 2; col++) {
                int storageRow = row;
                addSlot(new ArcanvilSlot(container, slot++, 22 + col * 17, 20 + row * 17) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return !isCasting() && switch (storageRow) {
                            case 0 -> CastMagicUtil.isPotion(stack);
                            case 1 -> CastMagicUtil.isCastMaterial(stack);
                            case 2 -> CastMagicUtil.isSmithingTemplate(stack);
                            case 3 -> CastMagicUtil.isScroll(stack);
                            default -> false;
                        };
                    }
                });
            }
        }

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
        addDataSlots(data);
    }

    public boolean isCasting() {
        return data.get(0) > 0;
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return Math.max(1, data.get(1));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (isCasting() && index < ARCANVIL_SLOT_COUNT) {
            return ItemStack.EMPTY;
        }

        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        result = stack.copy();
        if (index < ARCANVIL_SLOT_COUNT) {
            if (!moveItemStackTo(stack, ARCANVIL_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (CastMagicUtil.isPotion(stack)) {
            if (!moveToCategory(stack, ArcanvilBlockEntity.POTION_SLOT, ArcanvilBlockEntity.STORAGE_POTION_START, ArcanvilBlockEntity.STORAGE_MINERAL_START)) {
                return ItemStack.EMPTY;
            }
        } else if (CastMagicUtil.isScroll(stack)) {
            if (!moveToCategory(stack, ArcanvilBlockEntity.SCROLL_SLOT, ArcanvilBlockEntity.STORAGE_SCROLL_START, ARCANVIL_SLOT_COUNT)) {
                return ItemStack.EMPTY;
            }
        } else if (CastMagicUtil.isCastableEquipment(stack)) {
            if (!moveItemStackTo(stack, ArcanvilBlockEntity.EQUIPMENT_SLOT, ArcanvilBlockEntity.EQUIPMENT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (CastMagicUtil.isSmithingTemplate(stack)) {
            if (!moveToCategory(stack, ArcanvilBlockEntity.TEMPLATE_SLOT, ArcanvilBlockEntity.STORAGE_TEMPLATE_START, ArcanvilBlockEntity.STORAGE_SCROLL_START)) {
                return ItemStack.EMPTY;
            }
        } else if (CastMagicUtil.isCastMaterial(stack)) {
            if (!moveToCategory(stack, ArcanvilBlockEntity.MINERAL_SLOT, ArcanvilBlockEntity.STORAGE_MINERAL_START, ArcanvilBlockEntity.STORAGE_TEMPLATE_START)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    private boolean moveToCategory(ItemStack stack, int primarySlot, int storageStart, int storageEnd) {
        boolean moved = moveItemStackTo(stack, primarySlot, primarySlot + 1, false);
        if (!stack.isEmpty()) {
            moved |= moveItemStackTo(stack, storageStart, storageEnd, false);
        }
        return moved;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().getBlockState(pos).is(ModBlocks.ARCANVIL_TABLE.get())
                && container.stillValid(player);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 118 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 176));
        }
    }

    private static Container containerAt(Inventory inventory, BlockPos pos) {
        if (inventory.player.level().getBlockEntity(pos) instanceof ArcanvilBlockEntity blockEntity) {
            return blockEntity;
        }
        return new SimpleContainer(ARCANVIL_SLOT_COUNT);
    }

    private static ContainerData dataAt(Inventory inventory, BlockPos pos) {
        if (inventory.player.level().getBlockEntity(pos) instanceof ArcanvilBlockEntity blockEntity) {
            return blockEntity.getData();
        }
        return new SimpleContainerData(2);
    }

    private class ArcanvilSlot extends Slot {
        ArcanvilSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return !isCasting() && super.mayPickup(player);
        }
    }
}
