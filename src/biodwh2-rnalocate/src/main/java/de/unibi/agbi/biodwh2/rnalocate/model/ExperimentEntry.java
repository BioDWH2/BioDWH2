package de.unibi.agbi.biodwh2.rnalocate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "RNALocate_ID", "Gene_ID", "Gene_Name", "Gene_symbol", "RNA_category", "Species", "PMID",
        "SubCellular_Localization", "Description"
})
public class ExperimentEntry {
    @JsonProperty("RNALocate_ID")
    public String id;
    @JsonProperty("Gene_ID")
    public String geneId;
    @JsonProperty("Gene_Name")
    public String geneName;
    @JsonProperty("Gene_symbol")
    public String geneSymbol;
    @JsonProperty("RNA_category")
    public String rnaCategory;
    @JsonProperty("Species")
    public String species;
    @JsonProperty("PMID")
    public Integer pmid;
    @JsonProperty("SubCellular_Localization")
    public String subCellularLocalization;
    @JsonProperty("Description")
    public String description;
}
