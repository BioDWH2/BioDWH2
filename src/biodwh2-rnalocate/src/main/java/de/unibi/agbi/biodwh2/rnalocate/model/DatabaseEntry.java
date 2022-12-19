package de.unibi.agbi.biodwh2.rnalocate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "RNALocate_ID", "Gene_ID", "Gene_symbol", "RNA_category", "Species", "Subcellular_localization", "Database"
})
public class DatabaseEntry {
    @JsonProperty("RNALocate_ID")
    public String id;
    @JsonProperty("Gene_ID")
    public String geneId;
    @JsonProperty("Gene_symbol")
    public String geneSymbol;
    @JsonProperty("RNA_category")
    public String rnaCategory;
    @JsonProperty("Species")
    public String species;
    @JsonProperty("Subcellular_localization")
    public String subCellularLocalization;
    @JsonProperty("Database")
    public String database;
}
