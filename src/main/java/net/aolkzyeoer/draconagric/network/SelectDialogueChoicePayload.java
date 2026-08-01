package net.aolkzyeoer.draconagric.network;

import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectDialogueChoicePayload(ResourceLocation dialogueId, int choiceIndex) implements CustomPacketPayload {
    public static final Type<SelectDialogueChoicePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "select_dialogue_choice"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectDialogueChoicePayload> STREAM_CODEC = StreamCodec.ofMember(SelectDialogueChoicePayload::write, SelectDialogueChoicePayload::read);

    private static SelectDialogueChoicePayload read(RegistryFriendlyByteBuf buffer) {
        return new SelectDialogueChoicePayload(buffer.readResourceLocation(), buffer.readVarInt());
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeResourceLocation(dialogueId);
        buffer.writeVarInt(choiceIndex);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
