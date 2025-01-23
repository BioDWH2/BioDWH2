package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PositionStatus {
    @JsonProperty("EQUAL") EQUAL,
    @JsonProperty("GREATER_THAN") GREATER_THAN,
    @JsonProperty("LESS_THAN") LESS_THAN
}
