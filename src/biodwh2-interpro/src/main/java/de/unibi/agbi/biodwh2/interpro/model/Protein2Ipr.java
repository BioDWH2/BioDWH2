package de.unibi.agbi.biodwh2.interpro.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "proteinAccession", "interproIdentifier", "interProName", "proteinDomainIdentifier", "start", "end"
})
public class Protein2Ipr {
    @JsonProperty("proteinAccession")
    public String proteinAccession;
    @JsonProperty("interproIdentifier")
    public String interproIdentifier;
    @JsonProperty("interProName")
    public String interProName;
    @JsonProperty("proteinDomainIdentifier")
    public String proteinDomainIdentifier;
    @JsonProperty("start")
    public Integer start;
    @JsonProperty("end")
    public Integer end;
}
