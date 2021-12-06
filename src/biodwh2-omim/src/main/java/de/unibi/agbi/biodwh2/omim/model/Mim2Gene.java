package de.unibi.agbi.biodwh2.omim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "MIM Number", "MIM Entry Type", "Entrez Gene ID (NCBI)", "Approved Gene Symbol (HGNC)",
        "Ensembl Gene ID (Ensembl)"
})
public class Mim2Gene {
    @JsonProperty("MIM Number")
    public String mimNumber;
    @JsonProperty("MIM Entry Type")
    public String mimEntryType;
    @JsonProperty("Entrez Gene ID (NCBI)")
    public String entrezGeneId;
    @JsonProperty("Approved Gene Symbol (HGNC)")
    public String approvedGeneSymbol;
    @JsonProperty("Ensembl Gene ID (Ensembl)")
    public String ensemblGeneId;
}
