$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force -Path 'src/main/java/net/aolkzyeoer/draconagric/dialogue' | Out-Null
New-Item -ItemType Directory -Force -Path 'src/main/java/net/aolkzyeoer/draconagric/network' | Out-Null
New-Item -ItemType Directory -Force -Path 'src/main/java/net/aolkzyeoer/draconagric/client/dialogue' | Out-Null
New-Item -ItemType Directory -Force -Path 'src/main/resources/data/draconagric/draconagric_dialogues' | Out-Null
New-Item -ItemType Directory -Force -Path 'src/main/resources/assets/draconagric/textures/gui/dialogue' | Out-Null
New-Item -ItemType Directory -Force -Path 'dialogue_system_patch' | Out-Null

@'
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
'@ | Set-Content -Encoding UTF8 'src/main/java/net/aolkzyeoer/draconagric/dialogue/DialogueDefinition.java'

@'
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
'@ | Set-Content -Encoding UTF8 'src/main/java/net/aolkzyeoer/draconagric/dialogue/DialogueChoice.java'

@'
package net.aolkzyeoer.draconagric.dialogue;

import net.minecraft.resources.ResourceLocation;

public record DialogueReward(ResourceLocation item, int count) {
}
'@ | Set-Content -Encoding UTF8 'src/main/java/net/aolkzyeoer/draconagric/dialogue/DialogueReward.java'

@'
package net.aolkzyeoer.draconagric.dialogue;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.aolkzyeoer.draconagric.Draconagric;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DialogueManager extends SimpleJsonResourceReloadListener {
    public static final DialogueManager INSTANCE = new DialogueManager();
    private static final Gson GSON = new Gson();
    private final Map<ResourceLocation, DialogueDefinition> byId = new HashMap<>();
    private final Map<ResourceLocation, DialogueDefinition> byAdvancement = new HashMap<>();

    private DialogueManager() {
        super(GSON, "draconagric_dialogues");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
        byId.clear();
        byAdvancement.clear();

        elements.forEach((id, element) -> {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(element, "dialogue");
                ResourceLocation advancement = ResourceLocation.parse(GsonHelper.getAsString(json, "advancement"));
                String speaker = GsonHelper.getAsString(json, "speaker", "???");
                String text = GsonHelper.getAsString(json, "text", "");
                ResourceLocation portrait = ResourceLocation.parse(GsonHelper.getAsString(json, "portrait", Draconagric.MOD_ID + ":textures/gui/dialogue/template_portrait.png"));
                List<DialogueChoice> choices = readChoices(json);
                DialogueDefinition definition = new DialogueDefinition(id, advancement, speaker, text, portrait, choices);
                byId.put(id, definition);
                byAdvancement.put(advancement, definition);
            } catch (Exception ex) {
                Draconagric.LOGGER.error("Failed to load dialogue {}", id, ex);
            }
        });

        Draconagric.LOGGER.info("Loaded {} advancement dialogues", byId.size());
    }

    public Optional<DialogueDefinition> byAdvancement(ResourceLocation advancement) {
        return Optional.ofNullable(byAdvancement.get(advancement));
    }

    public Optional<DialogueDefinition> byId(ResourceLocation id) {
        return Optional.ofNullable(byId.get(id));
    }

    private static List<DialogueChoice> readChoices(JsonObject json) {
        List<DialogueChoice> choices = new ArrayList<>();
        JsonArray array = GsonHelper.getAsJsonArray(json, "choices", new JsonArray());
        for (JsonElement element : array) {
            JsonObject choiceJson = GsonHelper.convertToJsonObject(element, "choice");
            String label = GsonHelper.getAsString(choiceJson, "label", "继续");
            ResourceLocation nextDialogue = choiceJson.has("next_dialogue") ? ResourceLocation.parse(GsonHelper.getAsString(choiceJson, "next_dialogue")) : null;
            choices.add(new DialogueChoice(label, readRewards(choiceJson), nextDialogue));
        }
        if (choices.isEmpty()) {
            choices.add(new DialogueChoice("继续", List.of(), null));
        }
        return choices;
    }

    private static List<DialogueReward> readRewards(JsonObject choiceJson) {
        List<DialogueReward> rewards = new ArrayList<>();
        JsonArray rewardArray = GsonHelper.getAsJsonArray(choiceJson, "rewards", new JsonArray());
        for (JsonElement rewardElement : rewardArray) {
            JsonObject rewardJson = GsonHelper.convertToJsonObject(rewardElement, "reward");
            ResourceLocation item = ResourceLocation.parse(GsonHelper.getAsString(rewardJson, "item"));
            int count = Math.max(1, GsonHelper.getAsInt(rewardJson, "count", 1));
            rewards.add(new DialogueReward(item, count));
        }
        return rewards;
    }
}
'@ | Set-Content -Encoding UTF8 'src/main/java/net/aolkzyeoer/draconagric/dialogue/DialogueManager.java'

@'
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
                Item item = BuiltInRegistries.ITEM.get(reward.item());
                ItemStack stack = new ItemStack(item, reward.count());
                if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
            if (choice.nextDialogue() != null) {
                DialogueManager.INSTANCE.byId(choice.nextDialogue()).ifPresent(next -> PacketDistributor.sendToPlayer(player, OpenDialoguePayload.from(next)));
            }
        });
    }
}
'@ | Set-Content -Encoding UTF8 'src/main/java/net/aolkzyeoer/draconagric/dialogue/DialogueServerActions.java'
