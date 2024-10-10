package de.unibi.agbi.biodwh2.qptm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Organism", "PMID", "UniProt accession", "Gene name", "Position", "PTM", "Sequence window", "Raw peptide",
        "Sample", "Condition", "Log2Ratio (peptide)", "P value (peptide)", "Log2Ratio (protein)", "P value (protein)",
        "Reliability", "fdr (peptide)"
})
@GraphNodeLabel("PTM")
public class Entry {
    @JsonProperty("Organism")
    public String organism;
    @JsonProperty("PMID")
    @GraphProperty("pmid")
    public Integer pmid;
    @JsonProperty("UniProt accession")
    public String uniProtAccession;
    @JsonProperty("Gene name")
    public String geneName;
    @JsonProperty("Position")
    @GraphProperty("position")
    public Integer position;
    @JsonProperty("PTM")
    @GraphProperty("type")
    public String ptm;
    @JsonProperty("Sequence window")
    @GraphProperty("sequence_window")
    public String sequenceWindow;
    @JsonProperty("Raw peptide")
    @GraphProperty("raw_peptide")
    public String rawPeptide;
    @JsonProperty("Sample")
    @GraphProperty("sample")
    public String sample;
    @JsonProperty("Condition")
    @GraphProperty("condition")
    public String condition;
    @JsonProperty("Log2Ratio (peptide)")
    @GraphProperty(value = "log2ratio_peptide", emptyPlaceholder = "-")
    public String log2RatioPeptide;
    @JsonProperty("P value (peptide)")
    @GraphProperty(value = "p_value_peptide", emptyPlaceholder = "-")
    public String pValuePeptide;
    @JsonProperty("Log2Ratio (protein)")
    @GraphProperty(value = "log2ratio_protein", emptyPlaceholder = "-")
    public String log2RatioProtein;
    @JsonProperty("P value (protein)")
    @GraphProperty(value = "p_value_protein", emptyPlaceholder = "-")
    public String pValueProtein;
    @JsonProperty("Reliability")
    @GraphProperty("reliability")
    public Integer reliability;
    @JsonProperty("fdr (peptide)")
    @GraphProperty("fdr_peptide")
    public String fdrPeptide;
}
