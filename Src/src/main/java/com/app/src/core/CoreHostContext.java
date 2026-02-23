package com.app.src.core;

import com.app.src.api.HostContext;
import javafx.scene.Node;

import java.util.*;

// The Core provides the actual implementation of the interface
public class CoreHostContext implements HostContext {
    private final Map<String, Node> pluginViews = new LinkedHashMap<>();
    private final Map<String, List<?>> sharedData = new HashMap<>();

    public void showNotification(String message) {
        // Example: Update a status bar in the Dashboard
        displayMessage(message);
    }

    @Override
    public void displayMessage(String message) {
        System.out.println("Plugin message: " + message);
    }

    @Override
    public Optional<List<?>> getSharedData(String key) {
        return Optional.ofNullable(sharedData.get(key));
    }

    @Override
    public void registerPluginView(String pluginId, Node view) {
        if (pluginId == null || pluginId.isBlank() || view == null) {
            return; // Ignore invalid registrations
        }
        pluginViews.put(pluginId, view);
    }

    /**
     * Convenience accessor so the host UI can render a plugin's main view.
     */
    public Optional<Node> getPluginView(String pluginId) {
        return Optional.ofNullable(pluginViews.get(pluginId));
    }

    /**
     * Allows the host to expose immutable shared data to plugins at runtime.
     */
    public void putSharedData(String key, List<?> data) {
        if (key == null || key.isBlank() || data == null) {
            return;
        }
        sharedData.put(key, List.copyOf(data));
    }

    // Add other methods here later, like getProjectList(), getCurrentUser(), etc.
}
