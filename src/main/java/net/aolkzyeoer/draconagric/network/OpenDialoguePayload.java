package net.aolkzyeoer.draconagric.network;

import net.aolkzyeoer.draconagric.Draconagric;
import net.aolkzyeoer.draconagric.dialogue.DialogueDefinition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record OpenDialoguePayload(
        ResourceLocation dialogueId,
        String speaker,
        String text,
        ResourceLocation portrait,
        List<String> choices
) implements CustomPacketPayload {
    public static final Type<OpenDialoguePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "open_dialogue"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDialoguePayload> STREAM_CODEC = StreamCodec.ofMember(OpenDialoguePayload::write, OpenDialoguePayload::read);

    public OpenDialoguePayload {
        choices = List.copyOf(choices);
    }

    public static OpenDialoguePayload from(DialogueDefinition definition) {
        return new OpenDialoguePayload(
                definition.id(),
                definition.speaker(),
                definition.text(),
                definition.portrait(),
                definition.choices().stream().map(choice -> choice.label()).toList()
        );
    }

    private static OpenDialoguePayload read(RegistryFriendlyByteBuf buffer) {
        ResourceLocation dialogueId = buffer.readResourceLocation();
        String speaker = buffer.readUtf(256);
        String text = buffer.readUtf(4096);
        ResourceLocation portrait = buffer.readResourceLocation();
        int size = buffer.readVarInt();
        List<String> choices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            choices.add(buffer.readUtf(256));
        }
        return new OpenDialoguePayload(dialogueId, speaker, text, portrait, choices);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeResourceLocation(dialogueId);
        buffer.writeUtf(speaker, 256);
        buffer.writeUtf(text, 4096);
        buffer.writeResourceLocation(portrait);
        buffer.writeVarInt(choices.size());
        for (String choice : choices) {
            buffer.writeUtf(choice, 256);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
