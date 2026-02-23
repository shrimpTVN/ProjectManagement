package com.app.src.core;

import com.app.src.api.HostContext;
import com.app.src.api.Plugin;
import javafx.scene.Node;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages PF4J plugins and exposes the shared host context for them.
 */
public class PluginHost implements HostContext, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(PluginHost.class.getName());
    private final PluginManager pluginManager;
    private final Map<String, Node> pluginViews = new LinkedHashMap<>();
    private final Map<String, List<?>> sharedData = new HashMap<>();
    private final List<Plugin> activePlugins = new ArrayList<>();

    public PluginHost() {
        Path pluginsRoot = Path.of("plugins");
        ensurePluginDirExists(pluginsRoot);
        pluginManager = new DefaultPluginManager(pluginsRoot);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        registerPlugins();
    }

    private void ensurePluginDirExists(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to create plugins directory", e);
            }
        }
    }

    private void registerPlugins() {
        for (Plugin plugin : pluginManager.getExtensions(Plugin.class)) {
            plugin.initialize(this);
            pluginViews.put(plugin.getId(), plugin.getView());
            activePlugins.add(plugin);
        }
    }

    public List<Plugin> getActivePlugins() {
        return Collections.unmodifiableList(activePlugins);
    }

    public Optional<Node> getPluginView(String pluginId) {
        return Optional.ofNullable(pluginViews.get(pluginId));
    }

    public void putSharedData(String key, List<?> data) {
        sharedData.put(key, List.copyOf(data));
    }

    @Override
    public void displayMessage(String message) {
        LOGGER.info("Plugin message: " + message);
    }

    @Override
    public Optional<List<?>> getSharedData(String key) {
        return Optional.ofNullable(sharedData.get(key));
    }

    @Override
    public void registerPluginView(String pluginId, Node view) {
        pluginViews.put(pluginId, view);
    }

    @Override
    public void close() {
        pluginManager.stopPlugins();
    }
}
