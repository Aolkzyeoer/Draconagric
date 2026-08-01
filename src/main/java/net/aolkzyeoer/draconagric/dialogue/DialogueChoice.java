package net.aolkzyeoer.draconagric.dialogue;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record DialogueChoice(
        String label,
        List<DialogueReward> rewards,
        ResourceLocation nextDialogue
) {
    public DialogueChoice {
        rewards = List.copyOf(rewards);
    }
}
