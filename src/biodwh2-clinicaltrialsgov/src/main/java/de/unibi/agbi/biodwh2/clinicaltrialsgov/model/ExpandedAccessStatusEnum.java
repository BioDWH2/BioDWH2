package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExpandedAccessStatusEnum {
    AVAILABLE("Available"),
    NO_LONGER_AVAILABLE("No longer available"),
    TEMPORARILY_NOT_AVAILABLE("Temporarily not available"),
    APPROVED_FOR_MARKETING("Approved for marketing");

    ExpandedAccessStatusEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
