package com.ronja.crm.ronjaclient.desktop;

import com.ronja.crm.ronjaclient.desktop.DesktopApplication.StageReadyEvent;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
    Stage stage = event.getStage();
  }
}
