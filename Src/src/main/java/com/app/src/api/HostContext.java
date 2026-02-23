package com.app.src.api;

import javafx.scene.Node;
import java.util.Optional;

//import com.app.model.User;
import java.util.List;

/**
 * Provides the environment and shared resources
 * that the Host offers to the Plugin.
 */
public interface HostContext {
    // Allows plugin to get information about the currently logged-in user
//    User getCurrentUser();

    // Allows plugin to display a message on the main Dashboard status bar
    void displayMessage(String message);

    // Provides access to shared data (e.g., list of all projects)
    // This allows the plugin to do its job (like statistics)
    Optional<List<?>> getSharedData(String key);

    // Register the UI node that should be shown when the plugin is activated.
    void registerPluginView(String pluginId, Node view);
}