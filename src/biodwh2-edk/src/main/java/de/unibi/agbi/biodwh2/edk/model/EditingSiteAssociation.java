package de.unibi.agbi.biodwh2.edk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "edited_gene", "ensembl_gene", "editing_level", "editing_type", "enzyme", "region", "region_detail",
        "chrom", "position", "hg19position", "hg38position", "nt_seq_position", "codon_change", "amino_acid_change",
        "amino_acid_seq_position", "species", "molecular_effect", "phenotype", "correlation", "disease",
        "disease_category", "tissue", "treatment", "cell_type", "cell_type_id", "cell_line", "strategy", "survival",
        "pmid"
})
public class EditingSiteAssociation {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("edited_gene")
    public String editedGene;
    @JsonProperty("ensembl_gene")
    public String ensemblGene;
    @JsonProperty("editing_level")
    public String editingLevel;
    @JsonProperty("editing_type")
    public String editingType;
    @JsonProperty("enzyme")
    public String enzyme;
    @JsonProperty("region")
    public String region;
    @JsonProperty("region_detail")
    public String regionDetail;
    @JsonProperty("chrom")
    public String chrom;
    @JsonProperty("position")
    public String position;
    @JsonProperty("hg19position")
    public String hg19position;
    @JsonProperty("hg38position")
    public String hg38position;
    @JsonProperty("nt_seq_position")
    public String ntSeqPosition;
    @JsonProperty("codon_change")
    public String codonChange;
    @JsonProperty("amino_acid_change")
    public String aminoAcidChange;
    @JsonProperty("amino_acid_seq_position")
    public String aminoAcidSeqPosition;
    @JsonProperty("species")
    public String species;
    @JsonProperty("molecular_effect")
    public String molecularEffect;
    @JsonProperty("phenotype")
    public String phenotype;
    @JsonProperty("correlation")
    public String correlation;
    @JsonProperty("disease")
    public String disease;
    @JsonProperty("disease_category")
    public String diseaseCategory;
    @JsonProperty("tissue")
    public String tissue;
    @JsonProperty("treatment")
    public String treatment;
    @JsonProperty("cell_type")
    public String cellType;
    @JsonProperty("cell_type_id")
    public String cellTypeId;
    @JsonProperty("cell_line")
    public String cellLine;
    @JsonProperty("strategy")
    public String strategy;
    @JsonProperty("survival")
    public String survival;
    @JsonProperty("pmid")
    public String pmid;
}
