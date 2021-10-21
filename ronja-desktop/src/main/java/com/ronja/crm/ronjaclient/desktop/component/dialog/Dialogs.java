package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.App;
import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.service.clientapi.CustomerWebClient;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.dto.RepresentativeMapper;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.util.Optional;

public class Dialogs {

  private Dialogs() {
  }

  public static void showRepresentativeDetailDialog(CustomerWebClient customerWebClient,
                                                    RepresentativeWebClient representativeWebClient,
                                                    RepresentativeTableView tableView,
                                                    boolean update,
                                                    RepresentativeMapper mapper) {
    var dialog = new RepresentativeDetailDialog(customerWebClient, representativeWebClient, tableView, mapper, update);
    dialog.showAndWait();
  }

  public static void showCustomerDetailDialog(CustomerWebClient customerWebClient,
                                              CustomerTableView tableView,
                                              boolean update) {
    var dialog = new CustomerDetailDialog(customerWebClient, tableView, update);
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
}
