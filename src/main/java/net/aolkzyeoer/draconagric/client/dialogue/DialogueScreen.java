package net.aolkzyeoer.draconagric.client.dialogue;

import com.mojang.blaze3d.platform.NativeImage;
import net.aolkzyeoer.draconagric.network.OpenDialoguePayload;
import net.aolkzyeoer.draconagric.network.SelectDialogueChoicePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DialogueScreen extends Screen {
    private static final Map<ResourceLocation, PortraitSize> PORTRAIT_SIZE_CACHE = new HashMap<>();

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
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int panelH = Math.min(168, Math.max(126, this.height / 4));
        int panelY = this.height - panelH;

        renderDialogueBackdrop(graphics, panelY);
        renderPortrait(graphics);
        renderDialogueText(graphics, font, panelY);
        renderChoices(graphics, font, mouseX, mouseY, panelY);
    }

    private void renderDialogueBackdrop(GuiGraphics graphics, int y) {
        graphics.fillGradient(0, y - 32, this.width, y, 0x00000000, 0xAA020205);
        graphics.fill(0, y, this.width, this.height, 0xB806070C);
        graphics.fill(0, y, this.width, y + 1, 0x55FFFFFF);
        graphics.fill(0, y + 1, this.width, y + 2, 0x333DA9FF);
    }

    private void renderDialogueText(GuiGraphics graphics, Font font, int y) {
        int contentX = 36;
        int textWidth = Math.max(160, this.width - getPortraitRenderBounds().width - 112);
        drawReadableString(graphics, font, Component.literal(speaker), contentX, y + 18, 0x9EE5FF);
        drawReadableString(graphics, font, Component.literal("DIALOGUE"), contentX, y + 32, 0xDCE7FF);
        graphics.drawWordWrap(font, Component.literal(text), contentX + 1, y + 55, textWidth, 0xE0000000);
        graphics.drawWordWrap(font, Component.literal(text), contentX, y + 54, textWidth, 0xFFFFFF);
    }

    private void drawReadableString(GuiGraphics graphics, Font font, Component text, int x, int y, int color) {
        graphics.drawString(font, text, x + 1, y + 1, 0xE0000000, false);
        graphics.drawString(font, text, x, y, color, false);
    }

    private void renderPortrait(GuiGraphics graphics) {
        PortraitSize source = getPortraitSourceSize();
        PortraitSize bounds = getPortraitRenderBounds();
        float scale = Math.min((float) bounds.width / source.width, (float) bounds.height / source.height);
        int portraitW = Math.max(1, Math.round(source.width * scale));
        int portraitH = Math.max(1, Math.round(source.height * scale));
        int portraitX = this.width - portraitW - 18;
        int portraitY = this.height - portraitH + 6;
        graphics.blit(portrait, portraitX, portraitY, 0.0F, 0.0F, portraitW, portraitH, source.width, source.height);
    }

    private PortraitSize getPortraitRenderBounds() {
        int widthBased = (int) (this.width * 0.38F);
        int heightBased = (int) (this.height * 0.88F);
        int minimum = Math.min(220, Math.max(132, this.width / 3));
        int maxWidth = Math.min(430, Math.max(minimum, widthBased));
        return new PortraitSize(maxWidth, heightBased);
    }

    private PortraitSize getPortraitSourceSize() {
        return PORTRAIT_SIZE_CACHE.computeIfAbsent(portrait, location -> {
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);
            if (resource.isEmpty()) {
                return new PortraitSize(256, 256);
            }

            try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
                return new PortraitSize(image.getWidth(), image.getHeight());
            } catch (IOException ex) {
                return new PortraitSize(256, 256);
            }
        });
    }

    private void renderChoices(GuiGraphics graphics, Font font, int mouseX, int mouseY, int panelY) {
        if (choices.isEmpty()) {
            return;
        }

        int choiceX = Math.max(this.width / 2, this.width - getPortraitRenderBounds().width - 240);
        int choiceY = panelY + 24;
        for (int i = 0; i < choices.size(); i++) {
            int y = choiceY + i * 18;
            boolean hovered = isChoiceHovered(i, mouseX, mouseY, choiceX, choiceY, font);
            int color = hovered ? 0xFFFFFF : 0xE4ECFF;
            String prefix = hovered ? "> " : "  ";
            drawReadableString(graphics, font, Component.literal(prefix + choices.get(i)), choiceX, y, color);
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
            int choiceX = Math.max(this.width / 2, this.width - getPortraitRenderBounds().width - 240);
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

    private record PortraitSize(int width, int height) {
    }
}
