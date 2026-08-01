package net.aolkzyeoer.draconagric.dialogue;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record DialogueDefinition(
        ResourceLocation id,
        ResourceLocation advancement,
        String speaker,
        String text,
        ResourceLocation portrait,
        List<DialogueChoice> choices
) {
    public DialogueDefinition {
        choices = List.copyOf(choices);
    }
}
