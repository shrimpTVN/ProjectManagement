package com.app.src.authentication;

import javafx.scene.Node;

public class VisibleManer {
    private static VisibleManer instance;
    private VisibleManer() {}
    public static VisibleManer getInstance() {
        if (instance == null) {
            instance = new VisibleManer();
        }

        return instance;
    }

    public static void hideNode(Node node)
    {
        node.setVisible(false);
        node.setManaged(false);
    }
}
