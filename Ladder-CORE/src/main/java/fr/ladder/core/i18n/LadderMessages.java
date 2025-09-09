package fr.ladder.core.i18n;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import fr.ladder.api.injector.annotation.Inject;
import fr.ladder.api.util.ReflectionUtils;
import fr.ladder.api.util.graph.IGraph;
import fr.ladder.core.LadderEngine;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LadderMessages {

    @Inject
    private static LadderEngine _engine;

    private final Map<String, String> _messages;

    public LadderMessages() {
        _messages = new HashMap<>();
    }

    public void loadAllMessages(JavaPlugin plugin) {
        // fetch language from config
        final String language = _engine.getConfig()
                .getString("language", "fr")
                .toLowerCase();
        try(IGraph graph = ReflectionUtils.getGraph(plugin)) {
            final Pattern pattern = Pattern.compile("lang/" + language + "/.*\\.json");
            // fetch all lang files
            graph.getResources(pattern).forEach(resource ->
                    this.loadAllMessages(plugin, resource)
            );
        }
    }

    public void loadAllMessages(JavaPlugin plugin, String filename) {
        try(InputStream inputStream = plugin.getResource(filename)) {
            if(inputStream == null) {
                plugin.getLogger().warning("| File '" + filename + "' wasn't found.");
                return;
            }

            int previousSize = _messages.size();
            try(JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
                this.load("", new JsonParser().parse(reader));
                plugin.getLogger().info("| All messages has been successfully loaded.");
                plugin.getLogger().info("| Number of loaded messages: " + (_messages.size() - previousSize));
            }
            catch(IOException e) {
                plugin.getLogger().severe("| File '" + filename + "' has a bad json syntax.");
            }
        } catch(IOException e) {
            // plugin.catchException("An error occurred while loading lang file: " + this.filename, e);
        }
    }

    private void load(String path, JsonElement element) {
        if(element instanceof JsonArray array) {
            StringBuilder message = new StringBuilder();
            for(int i = 0; i < array.size(); i++) {
                message.append(array.get(i).getAsString());
                if(i != array.size() - 1)
                    message.append("\n");
            }
            _messages.put(path.substring(1), message.toString());
        } else if(element instanceof JsonObject obj) {
            obj.entrySet().forEach(member -> this.load(path + "." + member.getKey(), member.getValue()));
        } else if(element.getAsString() != null) {
            _messages.put(path.substring(1), element.getAsString());
        }
    }
}
