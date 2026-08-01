package net.aolkzyeoer.draconagric.client.dialogue;

import net.aolkzyeoer.draconagric.network.OpenDialoguePayload;
import net.minecraft.client.Minecraft;

public final class ClientDialogueOverlay {
    private ClientDialogueOverlay() {
    }

    public static void open(OpenDialoguePayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new DialogueScreen(payload));
    }
}
