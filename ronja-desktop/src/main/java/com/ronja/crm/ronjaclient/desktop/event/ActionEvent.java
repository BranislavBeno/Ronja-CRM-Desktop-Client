package com.ronja.crm.ronjaclient.desktop.event;

public class ActionEvent extends AppEvent<ActionEventId> {

    public ActionEvent(ActionEventId eventSourceId) {
        super(eventSourceId);
    }
}
