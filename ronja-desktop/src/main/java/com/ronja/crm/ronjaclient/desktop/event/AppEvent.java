package com.ronja.crm.ronjaclient.desktop.event;

import java.util.Objects;

public abstract class AppEvent<T extends AppEventId> {
    private final T eventId;

    protected AppEvent(T eventId) {
        this.eventId = Objects.requireNonNull(eventId);
    }

    public T getEventId() {
        return eventId;
    }
}
