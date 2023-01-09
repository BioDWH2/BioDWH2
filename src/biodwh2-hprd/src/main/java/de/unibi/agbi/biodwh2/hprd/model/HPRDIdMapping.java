package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "hprd_id", "geneSymbol", "nucleotide_accession", "protein_accession", "entrezgene_id", "omim_id",
        "swissprot_id", "main_name"
})
public class HPRDIdMapping {
    @JsonProperty("hprd_id")
    public String hprdId;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("nucleotide_accession")
    public String nucleotideAccession;
    @JsonProperty("protein_accession")
    public String proteinAccession;
    @JsonProperty("entrezgene_id")
    public String entrezGeneId;
    @JsonProperty("omim_id")
    public String omimId;
    @JsonProperty("swissprot_id")
    public String swissProtId;
    @JsonProperty("main_name")
    public String mainName;
}
