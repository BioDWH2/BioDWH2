package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_mirna", "xsome", "contig_start", "contig_end", "strand"})
public class MirnaChromosomeBuild {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("xsome")
    public String xsome;
    @JsonProperty("contig_start")
    public Long contigStart;
    @JsonProperty("contig_end")
    public Long contigEnd;
    @JsonProperty("strand")
    public String strand;
}
