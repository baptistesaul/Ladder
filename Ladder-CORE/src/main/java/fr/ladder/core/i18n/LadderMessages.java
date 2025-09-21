package fr.ladder.core.i18n;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import fr.ladder.api.LadderAPI;
import fr.ladder.api.i18n.Messages;
import fr.ladder.api.i18n.Var;
import fr.ladder.api.injector.annotation.Inject;
import fr.ladder.api.util.ReflectionUtils;
import fr.ladder.api.util.graph.IGraph;
import fr.ladder.core.LadderEngine;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LadderMessages implements Messages.Implementation {

    @Inject
    private static LadderEngine _engine;

    private final Map<String, String> _messages;

    private final Collection<Var> defaultVars = new ArrayList<>();

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

            int previousSize = _messages.size();
            graph.getResources(pattern).forEach(resource -> this.loadAllMessages(plugin, resource));
            plugin.getLogger().info("| All messages has been successfully loaded.");
            plugin.getLogger().info("| Number of loaded messages: " + (_messages.size() - previousSize));
        }
    }

    private void loadAllMessages(JavaPlugin plugin, String filename) {
        try(InputStream inputStream = plugin.getResource(filename)) {
            if(inputStream == null) {
                plugin.getLogger().warning("| File '" + filename + "' wasn't found.");
                return;
            }

            plugin.getLogger().info("| Loading messages of '" + filename + "'.");
            try(JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
                this.load("", new JsonParser().parse(reader));
            }
            catch(JsonSyntaxException e) {
                LadderAPI.catchException(plugin.getLogger(), "| File '" + filename + "' has a bad json syntax.", e);
            }
        } catch(IOException e) {
            LadderAPI.catchException(plugin.getLogger(), "An error occurred while loading lang file: " + filename, e);
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
        } else if(element instanceof JsonPrimitive primitive && primitive.isString()) {
            _messages.put(path.substring(1), element.getAsString());
        }
    }

    private String formatMessage(String message, Var... vars) {
        for(Var var : vars) {
            message = message.replace('{' + var.key() + '}', var.value());
        }

        for(Var var : this.defaultVars) {
            message = message.replace('{' + var.key() + '}', var.value());
        }

        return message.replace("\t", "  ");
    }

    @Override
    public boolean exists(String path) {
        return _messages.containsKey(path);
    }

    @Override
    public String get(String path, Var... vars) {
        String message = _messages.get(path);
        if(message == null)
            throw new IllegalArgumentException("Missing message for the path: \"" + path + "\"");

        return this.formatMessage(message, vars);
    }

    @Override
    public String[] array(String path, Var... vars) {
        return this.get(path, vars).split("\n");
    }

    @Override
    public void addDefaultVariable(String key, Object value) {
        this.defaultVars.add(Var.of(key, value));
    }
}
