package de.unibi.agbi.biodwh2.qptmplants.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Protein ID", "Gene name", "Position", "Raw peptide", "Sequence window", "Modification",
        "Localization probability", "Sample", "Condition", "Log2ratio", "P value", "Organism", "PMID"
})
public class Entry {
    @JsonProperty("Protein ID")
    public String proteinId;
    @JsonProperty("Gene name")
    public String geneName;
    @JsonProperty("Position")
    public Integer position;
    @JsonProperty("Raw peptide")
    @GraphProperty("raw_peptide")
    public String rawPeptide;
    @JsonProperty("Sequence window")
    public String sequenceWindow;
    @JsonProperty("Modification")
    public String modification;
    @JsonProperty("Localization probability")
    @GraphProperty(value = "localization_probability", emptyPlaceholder = "N/A")
    public String localizationProbability;
    @JsonProperty("Sample")
    @GraphProperty("sample")
    public String sample;
    @JsonProperty("Condition")
    @GraphProperty("condition")
    public String condition;
    @JsonProperty("Log2ratio")
    @GraphProperty(value = "p_value", emptyPlaceholder = "N/A")
    public String log2Ratio;
    @JsonProperty("P value")
    @GraphProperty(value = "p_value", emptyPlaceholder = "N/A")
    public String pValue;
    @JsonProperty("Organism")
    public String organism;
    @JsonProperty("PMID")
    @GraphProperty("pmid")
    public Integer pmid;
}
