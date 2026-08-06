package net.aolkzyeoer.draconagric.client.castmagic;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.menu.ArcanvilMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ArcanvilScreen extends AbstractContainerScreen<ArcanvilMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "textures/gui/arcanvil_table.png");
    private static final ResourceLocation VANILLA_CONTAINER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
    private static final int ARCANVIL_TEXTURE_HEIGHT = 108;
    private static final int PLAYER_INVENTORY_TOP = 110;
    private static final int PLAYER_INVENTORY_HEIGHT = 90;
    private static final int PROGRESS_X = 7;
    private static final int PROGRESS_Y = 100;
    private static final int PROGRESS_WIDTH = 63;
    private static final int PROGRESS_HEIGHT = 4;
    private static final int TITLE_MAX_WIDTH = 63;

    public ArcanvilScreen(ArcanvilMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = PLAYER_INVENTORY_TOP + PLAYER_INVENTORY_HEIGHT;
        this.titleLabelX = 9;
        this.titleLabelY = 3;
        this.inventoryLabelX = 0;
        this.inventoryLabelY = -1000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, ARCANVIL_TEXTURE_HEIGHT, 176, 166);
        renderOccupiedSlotCovers(guiGraphics);
        guiGraphics.blit(VANILLA_CONTAINER, leftPos, topPos + PLAYER_INVENTORY_TOP, 0, 132, imageWidth, PLAYER_INVENTORY_HEIGHT, 256, 256);
        renderProgress(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String visibleTitle = font.plainSubstrByWidth(title.getString(), TITLE_MAX_WIDTH);
        guiGraphics.drawString(font, visibleTitle, titleLabelX, titleLabelY, 0xFFFFFFFF, true);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderProgress(GuiGraphics guiGraphics) {
        if (!menu.isCasting()) {
            return;
        }
        int width = Math.min(PROGRESS_WIDTH, menu.getProgress() * PROGRESS_WIDTH / menu.getMaxProgress());
        guiGraphics.fill(
                leftPos + PROGRESS_X,
                topPos + PROGRESS_Y,
                leftPos + PROGRESS_X + width,
                topPos + PROGRESS_Y + PROGRESS_HEIGHT,
                0xFFFFFFFF
        );
    }

    private void renderOccupiedSlotCovers(GuiGraphics guiGraphics) {
        for (int slotIndex = 0; slotIndex < 5; slotIndex++) {
            Slot slot = menu.getSlot(slotIndex);
            if (!slot.hasItem()) {
                continue;
            }

            for (int row = 1; row < 15; row++) {
                int color = slot.y + row < 36 ? topPanelColor(slot.y + row) : 0xFF900F78;
                guiGraphics.fill(
                        leftPos + slot.x + 1,
                        topPos + slot.y + row,
                        leftPos + slot.x + 15,
                        topPos + slot.y + row + 1,
                        color
                );
            }
        }
    }

    private static int topPanelColor(int y) {
        if (y < 23) {
            return 0xFFE244AD;
        }
        if (y < 27) {
            return 0xFFD239A2;
        }
        if (y < 31) {
            return 0xFFC12F98;
        }
        return 0xFFB2228C;
    }
}
