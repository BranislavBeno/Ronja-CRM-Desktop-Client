package com.ronja.crm.ronjaclient.desktop;

import com.ronja.crm.ronjaclient.desktop.component.splashscreen.SplashScreenPreLoader;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RonjaDesktopApplication {

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashScreenPreLoader.class.getCanonicalName());
        Application.launch(DesktopApplication.class, args);
    }
}
