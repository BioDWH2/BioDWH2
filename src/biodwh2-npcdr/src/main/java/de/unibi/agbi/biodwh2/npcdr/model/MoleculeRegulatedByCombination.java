package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "ProteinID", "Protein_Name", "Synonyms", "Gene_Name", "Uniprot", "UniprotAC", "Gene_ID", "EC_number",
        "TC_number", "Pfam", "Sequence", "Function", "TTDID", "keggid", "Intede", "Varidt", "miRBase"
})
public class MoleculeRegulatedByCombination {
    @JsonProperty("ProteinID")
    public String proteinId;
    @JsonProperty("Protein_Name")
    public String proteinName;
    @JsonProperty("Synonyms")
    public String synonyms;
    @JsonProperty("Gene_Name")
    public String geneName;
    @JsonProperty("Uniprot")
    public String uniprot;
    @JsonProperty("UniprotAC")
    public String uniprotAccession;
    @JsonProperty("Gene_ID")
    public String geneId;
    @JsonProperty("EC_number")
    public String ecNumber;
    @JsonProperty("TC_number")
    public String tcNumber;
    @JsonProperty("Pfam")
    public String pfam;
    @JsonProperty("Sequence")
    public String sequence;
    @JsonProperty("Function")
    public String function;
    @JsonProperty("TTDID")
    public String ttdId;
    @JsonProperty("keggid")
    public String keggId;
    @JsonProperty("Intede")
    public String intede;
    @JsonProperty("Varidt")
    public String varidt;
    @JsonProperty("miRBase")
    public String mirBase;
}
