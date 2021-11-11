package com.ronja.crm.ronjaclient.desktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS;

public class SplashScreenController implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setProgress(INDETERMINATE_PROGRESS);
    }
}
