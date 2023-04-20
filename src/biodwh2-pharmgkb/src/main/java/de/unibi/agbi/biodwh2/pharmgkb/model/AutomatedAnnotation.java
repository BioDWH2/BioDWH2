package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("AutomatedAnnotation")
@JsonPropertyOrder({
        "Chemical ID", "Chemical Name", "Chemical in Text", "Variation ID", "Variation Name", "Variation Type",
        "Variation in Text", "Gene IDs", "Gene Symbols", "Gene in Text", "Literature ID", "PMID", "Literature Title",
        "Publication Year", "Journal", "Sentence", "Source"
})
public class AutomatedAnnotation {
    @JsonProperty("Chemical ID")
    public String chemicalId;
    @JsonProperty("Chemical Name")
    public String chemicalName;
    @JsonProperty("Chemical in Text")
    @GraphProperty("chemical_in_text")
    public String chemicalInText;
    @JsonProperty("Variation ID")
    public String variationId;
    @JsonProperty("Variation Name")
    public String variationName;
    @JsonProperty("Variation Type")
    public String variationType;
    @JsonProperty("Variation in Text")
    @GraphProperty("variation_in_text")
    public String variationInText;
    @JsonProperty("Gene IDs")
    public String geneIds;
    @JsonProperty("Gene Symbols")
    public String geneSymbols;
    @JsonProperty("Gene in Text")
    @GraphProperty("gene_in_text")
    public String geneInText;
    @JsonProperty("Literature ID")
    public String literatureId;
    @JsonProperty("PMID")
    public String pmid;
    @JsonProperty("Literature Title")
    public String literatureTitle;
    @JsonProperty("Publication Year")
    public Integer publicationYear;
    @JsonProperty("Journal")
    @GraphProperty("journal")
    public String journal;
    @JsonProperty("Sentence")
    @GraphProperty("sentences")
    public String sentences;
    @JsonProperty("Source")
    @GraphProperty("source")
    public String source;
}
