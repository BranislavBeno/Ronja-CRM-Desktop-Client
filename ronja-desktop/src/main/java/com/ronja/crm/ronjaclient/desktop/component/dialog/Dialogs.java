package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.common.AppInfo;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Optional;

public class Dialogs {

    private Dialogs() {
    }

    public static void showRepresentativeDetailDialog(CustomerWebClient customerWebClient,
                                                      RepresentativeWebClient representativeWebClient,
                                                      RepresentativeTableView representativeView,
                                                      RepresentativeMapper representativeMapper,
                                                      boolean update,
                                                      boolean forDialog) {
        var dialog = new RepresentativeDetailDialog(
                customerWebClient,
                representativeWebClient, representativeView, representativeMapper,
                update, forDialog);
        dialog.showAndWait();
    }

    public static void showCustomerDetailDialog(CustomerWebClient customerWebClient,
                                                CustomerTableView customerTableView,
                                                RepresentativeTableView representativeTableView,
                                                boolean update) {
        var dialog = new CustomerDetailDialog(customerWebClient, customerTableView, representativeTableView, update);
        dialog.showAndWait();
    }

    public static void showErrorMessage(String title, String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setResizable(true);
        alert.initOwner(App.getMainWindow());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        alert.showAndWait();
    }

    public static boolean showAlertDialog(String title, String contentText, Alert.AlertType alertType) {
        var alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.initOwner(App.getMainWindow());
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        var noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        noButton.setText("Nie");
        var yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setText("Áno");
        noButton.setDefaultButton(true);
        yesButton.setDefaultButton(false);
        Optional<ButtonType> result = alert.showAndWait();

        return result.orElse(null) == ButtonType.YES;
    }

    public static void showAboutDialog(AppInfo appInfo) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("O aplikácii");
        alert.setHeaderText(appInfo.appTitle());
        alert.initOwner(App.getMainWindow());

        String date = appInfo.date().format(DateTimeUtil.DATE_TIME_FORMATTER);
        String copyrightSymbol = Character.toString(169);
        alert.setContentText("""
                Verzia: %s
                Dátum: %s
                Commit: %s
                                
                %s 2021-2022 Copyright: Branislav Beňo
                """.formatted(appInfo.version(), date, appInfo.commitId(), copyrightSymbol));
        alert.showAndWait();
    }
}
