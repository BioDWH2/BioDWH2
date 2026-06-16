package de.unibi.agbi.biodwh2.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * CARD_Model representing a CARD (Comprehensive Antibiotic Resistance Database) model entry
 */
public final class CARD_Model {
    @JsonProperty("model_id")
    public String modelId;

    @JsonProperty("model_name")
    public String modelName;

    @JsonProperty("model_type")
    public String modelType;

    @JsonProperty("model_type_id")
    public String modelTypeId;

    @JsonProperty("model_description")
    public String modelDescription;

    @JsonProperty("ARO_accession")
    public String aroAccession;

    @JsonProperty("ARO_id")
    public String aroId;

    @JsonProperty("ARO_name")
    public String aroName;

    @JsonProperty("CARD_short_name")
    public String cardShortName;

    @JsonProperty("ARO_description")
    public String aroDescription;

    @JsonProperty("model_param")
    public Map<String, Object> modelParam;

    @JsonProperty("model_sequences")
    public ModelSequences modelSequences;

    /**
     * ARO category entries grouped by an arbitrary key. Each value contains details about a specific ARO category.
     */
    @JsonProperty("ARO_category")
    public Map<String, AROCategory> aroCategory;

    public static final class AROCategory {
        @JsonProperty("category_aro_accession")
        public String categoryAroAccession;

        @JsonProperty("category_aro_cvterm_id")
        public String categoryAroCvtermId;

        @JsonProperty("category_aro_name")
        public String categoryAroName;

        @JsonProperty("category_aro_description")
        public String categoryAroDescription;

        @JsonProperty("category_aro_class_name")
        public String categoryAroClassName;
    }

    /**
     * Model sequences linking a protein sequence, its coding DNA sequence and the source organism. Sequence entries are
     * grouped by an arbitrary sequence id key.
     */
    public static final class ModelSequences {
        @JsonProperty("sequence")
        public Map<String, ModelSequence> sequence;
    }

    public static final class ModelSequence {
        @JsonProperty("protein_sequence")
        public ProteinSequence proteinSequence;

        @JsonProperty("dna_sequence")
        public DnaSequence dnaSequence;

        @JsonProperty("NCBI_taxonomy")
        public NcbiTaxonomy ncbiTaxonomy;
    }

    public static final class ProteinSequence {
        @JsonProperty("accession")
        public String accession;

        @JsonProperty("sequence")
        public String sequence;
    }

    public static final class DnaSequence {
        @JsonProperty("accession")
        public String accession;

        @JsonProperty("fmin")
        public String fmin;

        @JsonProperty("fmax")
        public String fmax;

        @JsonProperty("strand")
        public String strand;

        @JsonProperty("sequence")
        public String sequence;

        @JsonProperty("partial")
        public String partial;
    }

    public static final class NcbiTaxonomy {
        @JsonProperty("NCBI_taxonomy_cvterm_id")
        public String ncbiTaxonomyCvtermId;

        @JsonProperty("NCBI_taxonomy_name")
        public String ncbiTaxonomyName;

        @JsonProperty("NCBI_taxonomy_id")
        public String ncbiTaxonomyId;
    }
}

