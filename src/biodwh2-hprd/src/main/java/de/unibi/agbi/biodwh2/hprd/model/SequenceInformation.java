package de.unibi.agbi.biodwh2.hprd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "hprd_id", "isoform_id", "geneSymbol", "nucleotide_accession", "orf_start", "orf_end", "protein_accession",
        "protein_length", "protein_molecular_weight"
})
public class SequenceInformation {
    @JsonProperty("hprd_id")
    public String hprdId;
    @JsonProperty("isoform_id")
    public String isoformId;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("nucleotide_accession")
    public String nucleotideAccession;
    @JsonProperty("orf_start")
    public Integer orfStart;
    @JsonProperty("orf_end")
    public Integer orfEnd;
    @JsonProperty("protein_accession")
    public String proteinAccession;
    @JsonProperty("protein_length")
    public Integer proteinLength;
    @JsonProperty("protein_molecular_weight")
    public String proteinMolecularWeight;
}
