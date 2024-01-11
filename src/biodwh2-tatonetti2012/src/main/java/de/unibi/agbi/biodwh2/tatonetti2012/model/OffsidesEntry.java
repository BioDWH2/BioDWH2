package de.unibi.agbi.biodwh2.tatonetti2012.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "stitch_id", "drug", "umls_id", "event", "rr", "log2rr", "t_statistic", "pvalue", "observed", "expected",
        "bg_correction", "sider", "future_aers", "medeffect"
})
public class OffsidesEntry {
    @JsonProperty("stitch_id")
    public String stitchId;
    @JsonProperty("drug")
    public String drug;
    @JsonProperty("umls_id")
    public String eventUmlsId;
    @JsonProperty("event")
    public String eventName;
    @JsonProperty("rr")
    public String rr;
    @JsonProperty("log2rr")
    public String log2rr;
    @JsonProperty("t_statistic")
    public String tStatistic;
    @JsonProperty("pvalue")
    public String pValue;
    @JsonProperty("observed")
    public String observed;
    @JsonProperty("expected")
    public String expected;
    @JsonProperty("bg_correction")
    public String bgCorrection;
    @JsonProperty("sider")
    public String sider;
    @JsonProperty("future_aers")
    public String futureAers;
    @JsonProperty("medeffect")
    public String medEffect;
}
