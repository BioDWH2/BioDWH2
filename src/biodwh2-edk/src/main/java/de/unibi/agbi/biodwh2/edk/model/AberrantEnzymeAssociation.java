package de.unibi.agbi.biodwh2.edk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "enzyme", "regulator", "aberrence", "target", "regulation", "molecular_effect", "editing_level",
        "phenotype", "correlation", "disease", "species", "treatment", "cell_type", "cell_ontology", "cell_line",
        "strategy", "pmid"
})
public class AberrantEnzymeAssociation {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("enzyme")
    public String enzyme;
    @JsonProperty("regulator")
    public String regulator;
    @JsonProperty("aberrence")
    public String aberrence;
    @JsonProperty("target")
    public String target;
    @JsonProperty("regulation")
    public String regulation;
    @JsonProperty("molecular_effect")
    public String molecularEffect;
    @JsonProperty("editing_level")
    public String editingLevel;
    @JsonProperty("phenotype")
    public String phenotype;
    @JsonProperty("correlation")
    public String correlation;
    @JsonProperty("disease")
    public String disease;
    @JsonProperty("species")
    public String species;
    @JsonProperty("treatment")
    public String treatment;
    @JsonProperty("cell_type")
    public String cellType;
    @JsonProperty("cell_ontology")
    public String cellOntology;
    @JsonProperty("cell_line")
    public String cellLine;
    @JsonProperty("strategy")
    public String strategy;
    @JsonProperty("pmid")
    public String pmid;
}
