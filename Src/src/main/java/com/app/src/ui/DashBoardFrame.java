package com.app.src.ui;

import com.app.src.api.Plugin;
import com.app.src.core.CoreHostContext;
import com.app.src.core.PluginLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.List;

class DashboardFrame extends Application {

    private TabPane pluginTabPane;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        pluginTabPane = new TabPane();
        root.setCenter(pluginTabPane);

        // 1. Initialize the Core Context
        CoreHostContext context = new CoreHostContext();

        // 2. Scan and Load Plugins
        // Make sure a folder named "plugins" exists in your project root!
        PluginLoader loader = new PluginLoader("plugins");
        List<Plugin> loadedPlugins = loader.loadPlugins();

        // 3. Initialize and Display each Plugin
        for (Plugin plugin : loadedPlugins) {
            try {
                // Pass the context to the plugin
                plugin.initialize(context);

                // Get the JavaFX Node (View) from the plugin and add it to a Tab
                Tab tab = new Tab(plugin.getPluginName());
                tab.setContent(plugin.getView());
                tab.setClosable(false);

                pluginTabPane.getTabs().add(tab);
            } catch (Exception e) {
                System.err.println("Error initializing plugin " + plugin.getPluginName());
            }
        }

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setTitle("Work Management Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}