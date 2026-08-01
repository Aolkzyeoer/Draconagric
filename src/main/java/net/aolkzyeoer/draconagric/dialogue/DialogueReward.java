package net.aolkzyeoer.draconagric.dialogue;

import net.minecraft.resources.ResourceLocation;

public record DialogueReward(
        ResourceLocation item,
        int count,
        int experience
) {
    public static DialogueReward item(ResourceLocation item, int count) {
        return new DialogueReward(item, Math.max(1, count), 0);
    }

    public static DialogueReward experience(int experience) {
        return new DialogueReward(null, 1, Math.max(0, experience));
    }
}
