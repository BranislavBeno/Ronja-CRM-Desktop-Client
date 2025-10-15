package com.ronja.crm.ronjaclient.desktop.component.dashboard;

import com.ronja.crm.ronjaclient.desktop.component.common.AppInfo;
import com.ronja.crm.ronjaclient.desktop.component.dialog.Dialogs;
import com.ronja.crm.ronjaclient.desktop.component.internationalization.I18nUtils;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;

public class DashboardPane extends SplitPane {

    private final ScheduledPane scheduledPane;
    private final MetalPane metalPane;
    private final AppInfo appInfo;

    public DashboardPane(ScheduledPane scheduledPane, MetalPane metalPane, AppInfo appInfo) {
        this.scheduledPane = scheduledPane;
        this.metalPane = metalPane;
        this.appInfo = appInfo;

        getItems().addAll(scheduledPane, metalPane);

        // setup context menu
        final ContextMenu contextMenu = setUpContextMenu();
        setOnContextMenuRequested(e -> contextMenu.show(this, e.getScreenX(), e.getScreenY()));
    }

    public void setUpPane() {
        scheduledPane.setUpPane();
        metalPane.setUpPane();
    }

    private ContextMenu setUpContextMenu() {
        // fetch all items from
        var refreshItem = I18nUtils.menuItemForValue("label.refresh.view");
        refreshItem.setOnAction(_ -> Platform.runLater(this::setUpPane));
        // show application info
        var aboutItem = new MenuItem(I18N.get("label.about.info") + "...");
        aboutItem.setOnAction(_ -> Dialogs.showAboutDialog(appInfo));

        // create context menu
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                refreshItem, new SeparatorMenuItem(),
                aboutItem);

        return contextMenu;
    }
}
