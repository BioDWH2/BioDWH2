package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RecruitmentStatusEnum {
    ACTIVE_NOT_RECRUITING("Active, not recruiting"),
    COMPLETED("Completed"),
    ENROLLING_BY_INVITATION("Enrolling by invitation"),
    NOT_YET_RECRUITING("Not yet recruiting"),
    RECRUITING("Recruiting"),
    SUSPENDED("Suspended"),
    TERMINATED("Terminated"),
    WITHDRAWN("Withdrawn");

    RecruitmentStatusEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
