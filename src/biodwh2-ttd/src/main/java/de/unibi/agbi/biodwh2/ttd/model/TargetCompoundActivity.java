package de.unibi.agbi.biodwh2.ttd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"TTD Target ID", "TTD Drug/Compound ID", "Pubchem CID", "Activity"})
public class TargetCompoundActivity {
    @JsonProperty("TTD Target ID")
    public String ttdTargetId;
    @JsonProperty("TTD Drug/Compound ID")
    public String ttdDrugId;
    @JsonProperty("Pubchem CID")
    public String pubchemCID; //Long?
    @JsonProperty("Activity")
    public String activity;
}
