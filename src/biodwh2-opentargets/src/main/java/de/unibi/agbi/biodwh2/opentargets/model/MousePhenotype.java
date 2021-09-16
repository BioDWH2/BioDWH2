package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MousePhenotype {
    @JsonProperty("id")
    public String id;
    @JsonProperty("phenotypes")
    public PhenotypeGene[] phenotypes;

    public static class PhenotypeGene {
        @JsonProperty("mouse_gene_id")
        public String mouseGeneId;
        @JsonProperty("mouse_gene_symbol")
        public String mouseGeneSymbol;
        @JsonProperty("phenotypes")
        public Phenotype[] phenotypes;
    }

    public static class Phenotype {
        @JsonProperty("category_mp_identifier")
        public String categoryMpIdentifier;
        @JsonProperty("category_mp_label")
        public String categoryMpLabel;
        @JsonProperty("genotype_phenotype")
        public GenotypePhenotype[] genotypePhenotype;
    }

    public static class GenotypePhenotype {
        @JsonProperty("mp_identifier")
        public String mpIdentifier;
        @JsonProperty("mp_label")
        public String mpLabel;
        @JsonProperty("pmid")
        public String pmid;
        @JsonProperty("subject_allelic_composition")
        public String subjectAllelicComposition;
        @JsonProperty("subject_background")
        public String subjectBackground;

    }
}
