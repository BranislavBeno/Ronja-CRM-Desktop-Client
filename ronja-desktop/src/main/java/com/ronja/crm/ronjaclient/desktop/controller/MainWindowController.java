package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.dashboard.DashboardPane;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import com.ronja.crm.ronjaclient.desktop.i18n.I18N;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MainWindowController {

    private final DashboardPane dashboardPane;
    private final CustomerTableView customerTableView;
    private final RepresentativeTableView representativeTableView;

    @FXML
    private Tab dashboardTab;
    @FXML
    private Tab customersTab;
    @FXML
    private Tab representativesTab;

    public MainWindowController(@Autowired DashboardPane dashboardPane,
                                @Autowired CustomerTableView customerTableView,
                                @Autowired RepresentativeTableView representativeTableView) {
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);

        this.dashboardPane = Objects.requireNonNull(dashboardPane);
        this.customerTableView = Objects.requireNonNull(customerTableView);
        this.representativeTableView = Objects.requireNonNull(representativeTableView);
    }

    @FXML
    public void initialize() {
        // dashboard tab
        dashboardTab.setContent(dashboardPane);
        dashboardTab.textProperty().bind(I18N.createStringBinding("label.tab.dashboard"));
        dashboardTab.selectedProperty().addListener((observable, oldValue, newValue) ->
                onChange(newValue, dashboardPane::setUpPane));
        // customers tab
        customersTab.setContent(customerTableView);
        customersTab.textProperty().bind(I18N.createStringBinding("label.tab.customers"));
        customersTab.selectedProperty().addListener((observable, oldValue, newValue) ->
                onChange(newValue, customerTableView::refreshItems));
        // representatives tab
        representativesTab.setContent(representativeTableView);
        representativesTab.textProperty().bind(I18N.createStringBinding("label.tab.representatives"));
        representativesTab.selectedProperty().addListener((observable, oldValue, newValue) ->
                onChange(newValue, representativeTableView::refreshItems));
    }

    private void onChange(Boolean newValue, Runnable runnable) {
        if (Boolean.TRUE.equals(newValue)) {
            runnable.run();
        }
    }

    private void handleUncaughtException(Thread thread, Throwable throwable) {
        Platform.runLater(
                () -> Dialogs.showErrorMessage("dialog.title.error", throwable.getMessage()));
    }
}
