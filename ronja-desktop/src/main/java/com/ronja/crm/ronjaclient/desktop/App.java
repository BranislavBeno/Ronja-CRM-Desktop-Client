package com.ronja.crm.ronjaclient.desktop;

import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Locale;

public class App {
    private static Window window;
    private static Stage stage;

    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    private App() {
    }

    public static Window getMainWindow() {
        return window;
    }

    public static Stage getMainStage() {
        return stage;
    }

    public static void setWindow(Window window) {
        App.window = window;
    }

    static void setStage(Stage stage) {
        App.stage = stage;
    }
}
