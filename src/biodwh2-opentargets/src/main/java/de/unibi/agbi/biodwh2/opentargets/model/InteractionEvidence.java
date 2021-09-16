package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InteractionEvidence {
    @JsonProperty("targetA")
    public String targetA;
    @JsonProperty("targetB")
    public String targetB;
    @JsonProperty("expansionMethodMiIdentifier")
    public String expansionMethodMiIdentifier;
    @JsonProperty("expansionMethodShortName")
    public String expansionMethodShortName;
    @JsonProperty("evidenceScore")
    public Double evidenceScore;
    @JsonProperty("interactionScore")
    public Double interactionScore;
    @JsonProperty("intABiologicalRole")
    public String intABiologicalRole;
    @JsonProperty("intBBiologicalRole")
    public String intBBiologicalRole;
    @JsonProperty("interactionDetectionMethodMiIdentifier")
    public String interactionDetectionMethodMiIdentifier;
    @JsonProperty("interactionDetectionMethodShortName")
    public String interactionDetectionMethodShortName;
    @JsonProperty("intA")
    public String intA;
    @JsonProperty("intB")
    public String intB;
    @JsonProperty("intASource")
    public String intASource;
    @JsonProperty("intBSource")
    public String intBSource;
    @JsonProperty("hostOrganismScientificName")
    public String hostOrganismScientificName;
    @JsonProperty("interactionTypeMiIdentifier")
    public String interactionTypeMiIdentifier;
    @JsonProperty("interactionTypeShortName")
    public String interactionTypeShortName;
    @JsonProperty("pubmedId")
    public String pubmedId;
    @JsonProperty("interactionIdentifier")
    public String interactionIdentifier;
    @JsonProperty("speciesA")
    public Species speciesA;
    @JsonProperty("speciesB")
    public Species speciesB;
    @JsonProperty("hostOrganismTaxId")
    public Integer hostOrganismTaxId;
    @JsonProperty("interactionResources")
    public InteractionResource interactionResources;
    @JsonProperty("participantDetectionMethodA")
    public DetectionMethod[] participantDetectionMethodA;
    @JsonProperty("participantDetectionMethodB")
    public DetectionMethod[] participantDetectionMethodB;

    public static class DetectionMethod {
        @JsonProperty("miIdentifier")
        public String miIdentifier;
        @JsonProperty("shortName")
        public String shortName;
    }

    public static class InteractionResource {
        @JsonProperty("databaseVersion")
        public String databaseVersion;
        @JsonProperty("sourceDatabase")
        public String sourceDatabase;
    }

    public static class Species {
        @JsonProperty("mnemonic")
        public String mnemonic;
        @JsonProperty("scientificName")
        public String scientificName;
        @JsonProperty("taxonId")
        public Integer taxonId;
    }
}
