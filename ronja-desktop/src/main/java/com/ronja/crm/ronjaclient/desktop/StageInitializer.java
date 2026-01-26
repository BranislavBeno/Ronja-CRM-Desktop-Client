package com.ronja.crm.ronjaclient.desktop;

import com.ronja.crm.ronjaclient.desktop.DesktopApplication.StageReadyEvent;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final String applicationTitle;
    private final Resource mainResource;
    private final ApplicationContext applicationContext;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                            @Value("${spring.application.ui.localization}") String localization,
                            @Value("classpath:/MainWindow.fxml") Resource mainResource,
                            ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.mainResource = mainResource;
        this.applicationContext = applicationContext;

        Locale locale = Locale.ENGLISH;
        if (localization.equals("SK")) {
            locale = Locale.of("sk", "SK");
        }
        I18N.setLocale(locale);
    }

    @Override
    public void onApplicationEvent(@NonNull StageReadyEvent event) {
        try {
            var fxmlLoader = new FXMLLoader(mainResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);

            var parent = (Parent) fxmlLoader.load();
            var stage = event.getStage();
            App.setStage(stage);

            var scene = new Scene(parent, 1000, 800);
            stage.setScene(scene);
            stage.setTitle(applicationTitle);

            App.setWindow(scene.getWindow());
            stage.show();

        } catch (IOException e) {
            throw new StageException(I18N.get("exception.stage.initialization"), e);
        }
    }
}
