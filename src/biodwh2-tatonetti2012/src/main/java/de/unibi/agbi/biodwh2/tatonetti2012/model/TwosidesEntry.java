package de.unibi.agbi.biodwh2.tatonetti2012.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "stitch_id1", "stitch_id2", "drug1", "drug2", "event_umls_id", "event_name", "proportional_reporting_ratio",
        "pvalue", "confidence", "drug1_prr", "drug2_prr", "observed", "expected"
})
public class TwosidesEntry {
    @JsonProperty("stitch_id1")
    public String stitchId1;
    @JsonProperty("stitch_id2")
    public String stitchId2;
    @JsonProperty("drug1")
    public String drug1;
    @JsonProperty("drug2")
    public String drug2;
    @JsonProperty("event_umls_id")
    public String eventUmlsId;
    @JsonProperty("event_name")
    public String eventName;
    @JsonProperty("proportional_reporting_ratio")
    public String proportionalReportingRatio;
    @JsonProperty("pvalue")
    public String pValue;
    @JsonProperty("confidence")
    public String confidence;
    @JsonProperty("drug1_prr")
    public String drug1Prr;
    @JsonProperty("drug2_prr")
    public String drug2Prr;
    @JsonProperty("observed")
    public String observed;
    @JsonProperty("expected")
    public String expected;
}
