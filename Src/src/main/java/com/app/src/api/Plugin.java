package com.app.src.api;

import javafx.scene.Node;

/**
 * Describes the contract every plugin must fulfill so the host can manage its lifecycle.
 */
public interface Plugin {
    // Unique ID for the plugin
    String getId();

    // Name displayed in the UI (e.g., "Project Statistics")
    String getName();

    // Called when the plugin is first loaded
    void initialize(HostContext context);

    // Returns the UI node the plugin wants to expose to the host dashboard.
    Node getView();

    // Logic to execute when the user clicks the plugin menu
    void start();

    // Clean up resources (threads, DB connections)
    void stop();

    String getPluginName();

}