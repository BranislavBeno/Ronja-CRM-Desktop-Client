package com.ronja.crm.ronjaclient.service.clientapi;

import java.util.Collection;

public record ValidationErrorResponse(Collection<Violation> violations) {
}
