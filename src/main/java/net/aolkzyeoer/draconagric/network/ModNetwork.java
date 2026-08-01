package net.aolkzyeoer.draconagric.network;

import net.aolkzyeoer.draconagric.client.dialogue.ClientDialogueOverlay;
import net.aolkzyeoer.draconagric.dialogue.DialogueServerActions;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(OpenDialoguePayload.TYPE, OpenDialoguePayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> ClientDialogueOverlay.open(payload)));
        registrar.playToServer(SelectDialogueChoicePayload.TYPE, SelectDialogueChoicePayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                DialogueServerActions.choose(serverPlayer, payload.dialogueId(), payload.choiceIndex());
            }
        }));
    }
}
