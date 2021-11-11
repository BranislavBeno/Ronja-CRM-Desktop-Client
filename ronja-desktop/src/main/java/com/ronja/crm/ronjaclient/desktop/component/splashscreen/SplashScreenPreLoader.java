package com.ronja.crm.ronjaclient.desktop.component.splashscreen;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class SplashScreenPreLoader extends Preloader {

    private Stage preLoaderStage;
    private Scene scene;

    @Override
    public void init() throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("SplashScreen.fxml")));
        scene = new Scene(parent);
    }

    @Override
    public void start(Stage primaryStage) {
        preLoaderStage = primaryStage;
        preLoaderStage.setScene(scene);
        preLoaderStage.initStyle(StageStyle.UNDECORATED);
        preLoaderStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // called after main application initialization and before main application start is called
        if (info.getType().equals(StateChangeNotification.Type.BEFORE_START)) {
            preLoaderStage.hide();
        }
    }
}
