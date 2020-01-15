package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId", "meddraName", "meddraCode", "level", "llr", "llrThreshold", "drugAe",
"drugNoAe", "noDrugAe", "NoDrugNoAe"})

public final class Faers {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("meddraName")
    public String meddraName;
    @JsonProperty("meddraCode")
    public String meddraCode;
    @JsonProperty("level")
    public String level;
    @JsonProperty("llr")
    public String llr;
    @JsonProperty("llrThreshold")
    public String llrThreshold;
    @JsonProperty("drugAe")
    public String drugAe;
    @JsonProperty("drugNoAe")
    public String drugNoAe;
    @JsonProperty("noDrugAe")
    public String noDrugAe;
    @JsonProperty("NoDrugNoAe")
    public String NoDrugNoAe;
}
