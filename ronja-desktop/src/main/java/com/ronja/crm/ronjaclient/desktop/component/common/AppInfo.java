package com.ronja.crm.ronjaclient.desktop.component.common;

import java.time.LocalDate;

public record AppInfo(String version, LocalDate date, String commitId, String appTitle) {
}
