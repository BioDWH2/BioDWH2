package de.unibi.agbi.biodwh2.ncbi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "#tax_id", "GeneID", "status", "RNA_nucleotide_accession.version", "RNA_nucleotide_gi",
        "protein_accession.version", "protein_gi", "genomic_nucleotide_accession.version", "genomic_nucleotide_gi",
        "start_position_on_the_genomic_accession", "end_position_on_the_genomic_accession", "orientation", "assembly",
        "mature_peptide_accession.version", "mature_peptide_gi", "Symbol"
})
public class GeneAccession {
    @JsonProperty("#tax_id")
    public String taxonomyId;
    @JsonProperty("GeneID")
    public String geneId;
    @JsonProperty("status")
    public String status;
    @JsonProperty("RNA_nucleotide_accession.version")
    public String rnaNucleotideAccessionVersion;
    @JsonProperty("RNA_nucleotide_gi")
    public String rnaNucleotideGi;
    @JsonProperty("protein_accession.version")
    public String proteinAccessionVersion;
    @JsonProperty("protein_gi")
    public String proteinGi;
    @JsonProperty("genomic_nucleotide_accession.version")
    public String genomicNucleotideAccessionVersion;
    @JsonProperty("genomic_nucleotide_gi")
    public String genomicNucleotideGi;
    @JsonProperty("start_position_on_the_genomic_accession")
    public String startPositionOnTheGenomicAccession;
    @JsonProperty("end_position_on_the_genomic_accession")
    public String endPositionOnTheGenomicAccession;
    @JsonProperty("orientation")
    public String orientation;
    @JsonProperty("assembly")
    public String assembly;
    @JsonProperty("mature_peptide_accession.version")
    public String maturePeptideAccessionVersion;
    @JsonProperty("mature_peptide_gi")
    public String maturePeptideGi;
    @JsonProperty("Symbol")
    public String symbol;
}
