package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TemplateDirection {
    @JsonProperty("FORWARD") FORWARD,
    @JsonProperty("REVERSE") REVERSE
}
