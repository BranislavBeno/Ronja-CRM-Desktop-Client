package com.ronja.crm.ronjaclient.desktop.service;

import com.ronja.crm.ronjaclient.desktop.event.ActionEvent;
import com.ronja.crm.ronjaclient.desktop.event.ActionEventId;
import com.ronja.crm.ronjaclient.desktop.event.AppEvent;
import com.ronja.crm.ronjaclient.desktop.event.AppEventHandler;
import javafx.application.Platform;

public record AppEventService(AppEventHandler eventHandler) implements AppEventHandler {

    private void dispatchEvent(AppEvent<?> event) {
        if (event instanceof ActionEvent actionEvent) {
            Platform.runLater(() -> eventHandler.onActionEvent(actionEvent));
        }
    }

    public void deleteCustomer() {
        dispatchEvent(new ActionEvent(ActionEventId.DELETE_CUSTOMER));
    }
}
