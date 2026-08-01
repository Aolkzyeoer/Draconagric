package net.aolkzyeoer.draconagric.dialogue;

import net.aolkzyeoer.draconagric.network.OpenDialoguePayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public final class DialogueServerActions {
    private DialogueServerActions() {
    }

    public static void openForAdvancement(ServerPlayer player, ResourceLocation advancement) {
        DialogueManager.INSTANCE.byAdvancement(advancement).ifPresent(dialogue -> PacketDistributor.sendToPlayer(player, OpenDialoguePayload.from(dialogue)));
    }

    public static void choose(ServerPlayer player, ResourceLocation dialogueId, int choiceIndex) {
        DialogueManager.INSTANCE.byId(dialogueId).ifPresent(dialogue -> {
            if (choiceIndex < 0 || choiceIndex >= dialogue.choices().size()) {
                return;
            }

            DialogueChoice choice = dialogue.choices().get(choiceIndex);
            for (DialogueReward reward : choice.rewards()) {
                giveReward(player, reward);
            }

            if (choice.nextDialogue() != null) {
                DialogueManager.INSTANCE.byId(choice.nextDialogue()).ifPresent(next -> PacketDistributor.sendToPlayer(player, OpenDialoguePayload.from(next)));
            }
        });
    }

    private static void giveReward(ServerPlayer player, DialogueReward reward) {
        if (reward.experience() > 0) {
            player.giveExperiencePoints(reward.experience());
        }

        if (reward.item() != null) {
            Item item = BuiltInRegistries.ITEM.get(reward.item());
            ItemStack stack = new ItemStack(item, reward.count());
            if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }
}
