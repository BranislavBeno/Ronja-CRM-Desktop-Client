package com.ronja.crm.ronjaclient.service.validation;

import java.util.Collection;

public record ValidationErrorResponse(Collection<Violation> violations) {
}
