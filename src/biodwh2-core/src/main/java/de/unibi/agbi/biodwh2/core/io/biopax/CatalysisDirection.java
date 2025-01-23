package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CatalysisDirection {
    @JsonProperty("LEFT_TO_RIGHT") LEFT_TO_RIGHT,
    @JsonProperty("RIGHT_TO_LEFT") RIGHT_TO_LEFT
}
