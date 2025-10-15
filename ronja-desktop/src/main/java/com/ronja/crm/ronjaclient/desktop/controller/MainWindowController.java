package com.ronja.crm.ronjaclient.desktop.controller;

import com.ronja.crm.ronjaclient.desktop.component.customer.CustomerTableView;
import com.ronja.crm.ronjaclient.desktop.component.dashboard.DashboardPane;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.internationalization.I18nUtils;
import com.ronja.crm.ronjaclient.desktop.component.representative.RepresentativeTableView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
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

    public MainWindowController(DashboardPane dashboardPane,
                                CustomerTableView customerTableView,
                                RepresentativeTableView representativeTableView) {
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);

        this.dashboardPane = Objects.requireNonNull(dashboardPane);
        this.customerTableView = Objects.requireNonNull(customerTableView);
        this.representativeTableView = Objects.requireNonNull(representativeTableView);
    }

    @FXML
    public void initialize() {
        // customers tab
        customersTab.setContent(customerTableView);
        customersTab.textProperty().bind(I18nUtils.createStringBinding("label.tab.customers"));
        customersTab.selectedProperty().addListener((_, _, newValue) ->
                onChange(newValue, customerTableView::refreshItems));
        // representatives tab
        representativesTab.setContent(representativeTableView);
        representativesTab.textProperty().bind(I18nUtils.createStringBinding("label.tab.representatives"));
        representativesTab.selectedProperty().addListener((_, _, newValue) ->
                onChange(newValue, representativeTableView::refreshItems));
        // dashboard tab
        dashboardTab.setContent(dashboardPane);
        dashboardTab.textProperty().bind(I18nUtils.createStringBinding("label.tab.dashboard"));
        dashboardTab.selectedProperty().addListener((_, _, newValue) ->
                onChange(newValue, dashboardPane::setUpPane));
    }

    private void onChange(Boolean newValue, Runnable runnable) {
        if (Boolean.TRUE.equals(newValue)) {
            runnable.run();
        }
    }

    private void handleUncaughtException(Thread thread, Throwable throwable) {
        Platform.runLater(
                () -> Dialogs.showErrorMessage("dialog.error.title", throwable));
    }
}
