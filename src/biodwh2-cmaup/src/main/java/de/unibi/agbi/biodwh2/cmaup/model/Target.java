package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "Target_ID", "Gene_Symbol", "Protein_Name", "Uniprot_ID", "ChEMBL_ID", "TTD_ID", "Target_Class_Level1",
        "Target_Class_Level2", "Target_Class_Level3"
})
@GraphNodeLabel("Target")
public class Target {
    @JsonProperty("Target_ID")
    @GraphProperty("id")
    public String id;
    @JsonProperty("Gene_Symbol")
    @GraphProperty("gene_symbol")
    public String geneSymbol;
    @JsonProperty("Protein_Name")
    @GraphProperty("protein_name")
    public String proteinName;
    @JsonProperty("Uniprot_ID")
    @GraphProperty("uniprot_id")
    public String uniprotId;
    @JsonProperty("ChEMBL_ID")
    @GraphProperty("chembl_id")
    public String chemblId;
    @JsonProperty("TTD_ID")
    @GraphProperty("ttd_id")
    public String ttdId;
    @JsonProperty("Target_Class_Level1")
    @GraphProperty("target_class_level1")
    public String targetClassLevel1;
    @JsonProperty("Target_Class_Level2")
    @GraphProperty("target_class_level2")
    public String targetClassLevel2;
    @JsonProperty("Target_Class_Level3")
    @GraphProperty("target_class_level3")
    public String targetClassLevel3;
}
