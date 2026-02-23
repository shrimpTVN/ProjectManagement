package com.app.src.core;

import com.app.src.api.Plugin; // Assuming you moved the interface to an API package
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private final String pluginDirectoryPath;

    public PluginLoader(String pluginDirectoryPath) {
        this.pluginDirectoryPath = pluginDirectoryPath;
    }

    public List<Plugin> loadPlugins() {
        List<Plugin> validPlugins = new ArrayList<>();
        File pluginDir = new File(pluginDirectoryPath);

        // 1. Find all JAR files in the directory
        File[] jarFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) return validPlugins;

        for (File jar : jarFiles) {
            try {
                // 2. Create a ClassLoader for this specific JAR
                URL jarUrl = jar.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());

                // 3. Open the JAR file to inspect its contents
                try (JarFile jarFile = new JarFile(jar)) {
                    Enumeration<JarEntry> entries = jarFile.entries();

                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().endsWith(".class")) {
                            // Convert file path (com/plugin/MyClass.class) to class name (com.plugin.MyClass)
                            String className = entry.getName().replace('/', '.').replace(".class", "");

                            // 4. Use Reflection to load the class
                            Class<?> loadedClass = classLoader.loadClass(className);

                            // 5. Check if it implements our Plugin interface and is not an interface itself
                            if (Plugin.class.isAssignableFrom(loadedClass) && !loadedClass.isInterface()) {
                                // Instantiate the plugin
                                Plugin pluginInstance = (Plugin) loadedClass.getDeclaredConstructor().newInstance();
                                validPlugins.add(pluginInstance);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // 6. Centralized Error Handling [cite: 56]
                System.err.println("Failed to load plugin from " + jar.getName() + ": " + e.getMessage());
                // In a real app, you might log this to a file or show a non-intrusive alert.
            }
        }
        return validPlugins;
    }
}