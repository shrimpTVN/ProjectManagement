package com.app.src.core;

public interface Plugin {
    // Unique ID for the plugin
    String getId();

    // Name displayed in the UI (e.g., "Project Statistics")
    String getName();

    // Called when the plugin is first loaded
    void initialize(HostContext context);

    // Logic to execute when the user clicks the plugin menu
    void start();

    // Clean up resources (threads, DB connections)
    void stop();
}