package com.ronja.crm.ronjaclient.desktop;

import com.ronja.crm.ronjaclient.desktop.DesktopApplication.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

  @Value("classpath:/MainWindow.fxml")
  private Resource mainResource;
  private final String applicationTitle;
  private final ApplicationContext applicationContext;

  public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
                          ApplicationContext applicationContext) {
    this.applicationTitle = applicationTitle;
    this.applicationContext = applicationContext;
  }

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
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
      throw new StageException("Stage initialization failed", e);
    }
  }
}
