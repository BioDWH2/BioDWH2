package de.unibi.agbi.biodwh2.cmaup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        // "Target_ID", "Gene_Symbol", "Protein_Name", "Uniprot_ID", "ChEMBL_ID", "TTD_ID", "if_DTP", "if_CYP", "if_therapeutic_target", "Target_Class_Level1", "Target_Class_Level2", "Target_Class_Level3", "Target_Class_level_displayed", "Target_type"
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
    @GraphProperty(value = "ttd_id", emptyPlaceholder = {"NA", "n.a."})
    public String ttdId;
    @JsonProperty("if_DTP")
    @GraphBooleanProperty(value = "is_dtp", truthValue = "1")
    public String ifDTP;
    @JsonProperty("if_CYP")
    @GraphBooleanProperty(value = "is_cyp", truthValue = "1")
    public String ifCYP;
    @JsonProperty("if_therapeutic_target")
    @GraphBooleanProperty(value = "is_therapeutic_target", truthValue = "1")
    public String ifTherapeuticTarget;
    @JsonProperty("Target_Class_Level1")
    @GraphProperty(value = "target_class_level1", emptyPlaceholder = {"NA", "n.a."})
    public String targetClassLevel1;
    @JsonProperty("Target_Class_Level2")
    @GraphProperty(value = "target_class_level2", emptyPlaceholder = {"NA", "n.a."})
    public String targetClassLevel2;
    @JsonProperty("Target_Class_Level3")
    @GraphProperty(value = "target_class_level3", emptyPlaceholder = {"NA", "n.a."})
    public String targetClassLevel3;
    @JsonProperty("Target_Class_level_displayed")
    @GraphProperty("target_class_level_displayed")
    public String targetClassLevelDisplayed;
    @JsonProperty("Target_type")
    @GraphProperty("type")
    public String targetType;
}
