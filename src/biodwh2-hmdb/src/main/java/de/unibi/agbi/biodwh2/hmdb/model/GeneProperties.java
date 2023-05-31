package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

public class GeneProperties {
    @JsonProperty("chromosome_location")
    @GraphProperty("chromosome_location")
    public String chromosomeLocation;
    @GraphProperty("locus")
    public String locus;
    @JsonProperty("gene_sequence")
    @GraphProperty("gene_sequence")
    public String sequence;
}
