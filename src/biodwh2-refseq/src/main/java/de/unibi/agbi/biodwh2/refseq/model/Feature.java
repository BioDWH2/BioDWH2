package de.unibi.agbi.biodwh2.refseq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "# feature", "class", "assembly", "assembly_unit", "seq_type", "chromosome", "genomic_accession", "start",
        "end", "strand", "product_accession", "non-redundant_refseq", "related_accession", "name", "symbol", "GeneID",
        "locus_tag", "feature_interval_length", "product_length", "attributes"
})
public class Feature {
    @JsonProperty("# feature")
    public String feature;
    @JsonProperty("class")
    public String featureClass;
    @JsonProperty("assembly")
    public String assembly;
    @JsonProperty("assembly_unit")
    public String assemblyUnit;
    @JsonProperty("seq_type")
    public String seqType;
    @JsonProperty("chromosome")
    public String chromosome;
    @JsonProperty("genomic_accession")
    public String genomicAccession;
    @JsonProperty("start")
    public String start;
    @JsonProperty("end")
    public String end;
    @JsonProperty("strand")
    public String strand;
    @JsonProperty("product_accession")
    public String productAccession;
    @JsonProperty("non-redundant_refseq")
    public String nonRedundantRefseq;
    @JsonProperty("related_accession")
    public String relatedAccession;
    @JsonProperty("name")
    public String name;
    @JsonProperty("symbol")
    public String symbol;
    @JsonProperty("GeneID")
    public String geneId;
    @JsonProperty("locus_tag")
    public String locusTag;
    @JsonProperty("feature_interval_length")
    public String featureIntervalLength;
    @JsonProperty("product_length")
    public String productLength;
    @JsonProperty("attributes")
    public String attributes;
}
