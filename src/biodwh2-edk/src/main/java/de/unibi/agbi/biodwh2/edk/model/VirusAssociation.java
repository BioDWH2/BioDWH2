package de.unibi.agbi.biodwh2.edk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "name", "abbr", "protein", "host", "classification", "enzyme", "edited_gene", "edited_gene_alias",
        "editing_site", "editing_type", "editing_level", "molecular_effect", "correlation", "regulation", "disease",
        "summary", "pmid", "dependent"
})
public class VirusAssociation {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("abbr")
    public String abbr;
    @JsonProperty("protein")
    public String protein;
    @JsonProperty("host")
    public String host;
    @JsonProperty("classification")
    public String classification;
    @JsonProperty("enzyme")
    public String enzyme;
    @JsonProperty("edited_gene")
    public String editedGene;
    @JsonProperty("edited_gene_alias")
    public String editedGeneAlias;
    @JsonProperty("editing_site")
    public String editingSite;
    @JsonProperty("editing_type")
    public String editingType;
    @JsonProperty("editing_level")
    public String editingLevel;
    @JsonProperty("molecular_effect")
    public String molecularEffect;
    @JsonProperty("correlation")
    public String correlation;
    @JsonProperty("regulation")
    public String regulation;
    @JsonProperty("disease")
    public String disease;
    @JsonProperty("summary")
    public String summary;
    @JsonProperty("pmid")
    public String pmid;
    @JsonProperty("dependent")
    public String dependent;
}
