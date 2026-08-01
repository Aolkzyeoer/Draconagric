package net.aolkzyeoer.draconagric.dialogue;

import com.google.gson.*;
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
    private static final Gson GSON = new GsonBuilder().create();
    public static final DialogueManager INSTANCE = new DialogueManager();
    private static final ResourceLocation DEFAULT_PORTRAIT = ResourceLocation.fromNamespaceAndPath(Draconagric.MOD_ID, "textures/gui/dialogue/template_portrait.png");

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
                ResourceLocation portrait = ResourceLocation.parse(GsonHelper.getAsString(json, "portrait", DEFAULT_PORTRAIT.toString()));
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
            String type = GsonHelper.getAsString(rewardJson, "type", rewardJson.has("item") ? "item" : "experience");
            if ("item".equals(type)) {
                ResourceLocation item = ResourceLocation.parse(GsonHelper.getAsString(rewardJson, "item"));
                int count = Math.max(1, GsonHelper.getAsInt(rewardJson, "count", 1));
                rewards.add(DialogueReward.item(item, count));
            } else if ("experience".equals(type)) {
                int amount = Math.max(0, GsonHelper.getAsInt(rewardJson, "amount", GsonHelper.getAsInt(rewardJson, "experience", 0)));
                rewards.add(DialogueReward.experience(amount));
            } else {
                Draconagric.LOGGER.warn("Unknown dialogue reward type: {}", type);
            }
        }
        return rewards;
    }
}
