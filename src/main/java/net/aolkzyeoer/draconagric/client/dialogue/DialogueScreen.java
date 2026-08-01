package net.aolkzyeoer.draconagric.client.dialogue;

import net.aolkzyeoer.draconagric.network.OpenDialoguePayload;
import net.aolkzyeoer.draconagric.network.SelectDialogueChoicePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class DialogueScreen extends Screen {
    private final ResourceLocation dialogueId;
    private final String speaker;
    private final String text;
    private final ResourceLocation portrait;
    private final List<String> choices;

    public DialogueScreen(OpenDialoguePayload payload) {
        super(Component.literal(payload.speaker()));
        this.dialogueId = payload.dialogueId();
        this.speaker = payload.speaker();
        this.text = payload.text();
        this.portrait = payload.portrait();
        this.choices = payload.choices();
    }

    @Override
    protected void init() {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int panelH = Math.min(168, Math.max(126, this.height / 4));
        int panelY = this.height - panelH;

        graphics.fill(0, 0, this.width, this.height, 0x18000000);
        renderPortrait(graphics);
        renderDialoguePanel(graphics, font, panelY, panelH);
        renderChoices(graphics, font, mouseX, mouseY, panelY);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderDialoguePanel(GuiGraphics graphics, Font font, int y, int height) {
        graphics.fillGradient(0, y - 36, this.width, this.height, 0x00000000, 0xEE020205);
        graphics.fill(0, y, this.width, this.height, 0xD806070C);
        graphics.fill(0, y, this.width, y + 1, 0x66FFFFFF);
        graphics.fill(0, y + 1, this.width, y + 2, 0x223DA9FF);

        int contentX = 36;
        int textWidth = Math.max(160, this.width - getPortraitSize() - 112);
        graphics.drawString(font, Component.literal(speaker), contentX, y + 18, 0x8BD7FF, false);
        graphics.drawString(font, Component.literal("DIALOGUE"), contentX, y + 32, 0x66FFFFFF, false);
        graphics.drawWordWrap(font, Component.literal(text), contentX, y + 54, textWidth, 0xF4F7FF);
    }

    private void renderPortrait(GuiGraphics graphics) {
        int portraitW = getPortraitSize();
        int portraitH = portraitW;
        int portraitX = this.width - portraitW - 12;
        int portraitY = this.height - portraitH;
        graphics.blit(portrait, portraitX, portraitY, 0.0F, 0.0F, portraitW, portraitH, portraitW, portraitH);
    }

    private int getPortraitSize() {
        return Math.min(300, Math.max(132, this.width / 4));
    }

    private void renderChoices(GuiGraphics graphics, Font font, int mouseX, int mouseY, int panelY) {
        if (choices.isEmpty()) {
            return;
        }

        int choiceX = Math.max(this.width / 2, this.width - getPortraitSize() - 240);
        int choiceY = panelY + 24;
        for (int i = 0; i < choices.size(); i++) {
            int y = choiceY + i * 18;
            boolean hovered = isChoiceHovered(i, mouseX, mouseY, choiceX, choiceY, font);
            int color = hovered ? 0xFFFFFF : 0xBFC7D5;
            String prefix = hovered ? "> " : "  ";
            graphics.drawString(font, Component.literal(prefix + choices.get(i)), choiceX, y, color, false);
        }
    }

    private boolean isChoiceHovered(int index, double mouseX, double mouseY, int choiceX, int choiceY, Font font) {
        if (index < 0 || index >= choices.size()) {
            return false;
        }

        int y = choiceY + index * 18;
        int width = font.width("> " + choices.get(index));
        return mouseX >= choiceX && mouseX <= choiceX + width + 8 && mouseY >= y - 3 && mouseY <= y + 12;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !choices.isEmpty()) {
            Font font = Minecraft.getInstance().font;
            int panelH = Math.min(168, Math.max(126, this.height / 4));
            int panelY = this.height - panelH;
            int choiceX = Math.max(this.width / 2, this.width - getPortraitSize() - 240);
            int choiceY = panelY + 24;

            for (int i = 0; i < choices.size(); i++) {
                if (isChoiceHovered(i, mouseX, mouseY, choiceX, choiceY, font)) {
                    PacketDistributor.sendToServer(new SelectDialogueChoicePayload(dialogueId, i));
                    Minecraft.getInstance().setScreen(null);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
