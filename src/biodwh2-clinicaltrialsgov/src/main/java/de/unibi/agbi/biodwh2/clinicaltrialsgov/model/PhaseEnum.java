package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PhaseEnum {
    N_A("N/A"),
    EARLY_PHASE_1("Early Phase 1"),
    PHASE_1("Phase 1"),
    PHASE_1_PHASE_2("Phase 1/Phase 2"),
    PHASE_2("Phase 2"),
    PHASE_2_PHASE_3("Phase 2/Phase 3"),
    PHASE_3("Phase 3"),
    PHASE_4("Phase 4");

    PhaseEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
