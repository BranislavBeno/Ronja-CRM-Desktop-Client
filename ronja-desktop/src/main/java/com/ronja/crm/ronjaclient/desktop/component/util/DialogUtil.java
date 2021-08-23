package com.ronja.crm.ronjaclient.desktop.component.util;

import javafx.scene.Scene;

public class DialogUtil {

  private DialogUtil() {
  }

  public static void cancelOperation(Scene scene) {
    scene.getWindow().hide();
  }
}
