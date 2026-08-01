package net.aolkzyeoer.draconagric.event;

import net.aolkzyeoer.draconagric.dialogue.DialogueManager;
import net.aolkzyeoer.draconagric.dialogue.DialogueServerActions;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

public final class DialogueEvents {
    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(DialogueManager.INSTANCE);
    }

    @SubscribeEvent
    public void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DialogueServerActions.openForAdvancement(player, event.getAdvancement().id());
        }
    }
}
