package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Spectrum {
    public String type;
    @JsonProperty("spectrum_id")
    public Integer spectrumId;
}
