package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.opentargets.etl.OpenTargetsGraphExporter;

import java.util.Map;

@GraphNodeLabel(OpenTargetsGraphExporter.MOLECULE_LABEL)
public class Molecule {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("parentId")
    public String parentId;
    @JsonProperty("canonicalSmiles")
    @GraphProperty("canonical_smiles")
    public String canonicalSmiles;
    @JsonProperty("inchiKey")
    @GraphProperty("inchi_key")
    public String inchiKey;
    @JsonProperty("drugType")
    @GraphProperty("drug_type")
    public String drugType;
    @JsonProperty("blackBoxWarning")
    @GraphProperty("black_box_warning")
    public Boolean blackBoxWarning;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("maximumClinicalTrialPhase")
    @GraphProperty("maximum_clinical_trial_phase")
    public Integer maximumClinicalTrialPhase;
    @JsonProperty("hasBeenWithdrawn")
    @GraphProperty("has_been_withdrawn")
    public Boolean hasBeenWithdrawn;
    @JsonProperty("isApproved")
    @GraphProperty("is_approved")
    public Boolean isApproved;
    @JsonProperty("tradeNames")
    @GraphProperty("trade_names")
    public String[] tradeNames;
    @JsonProperty("synonyms")
    @GraphProperty("synonyms")
    public String[] synonyms;
    @JsonProperty("childChemblIds")
    public String[] childChemblIds;
    @JsonProperty("yearOfFirstApproval")
    @GraphProperty("year_of_first_approval")
    public Integer yearOfFirstApproval;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("linkedTargets")
    public LinkedElements linkedTargets;
    @JsonProperty("linkedDiseases")
    public LinkedElements linkedDiseases;
    @JsonProperty("crossReferences")
    public Map<String, String[]> crossReferences;
    @JsonProperty("withdrawnNotice")
    public WithdrawnNotice withdrawnNotice;

    public static class LinkedElements {
        @JsonProperty("count")
        public Integer count;
        @JsonProperty("rows")
        public String[] rows;
    }

    public static class WithdrawnNotice {
        @JsonProperty("countries")
        public String[] countries;
        @JsonProperty("classes")
        public String[] classes;
        @JsonProperty("year")
        public Integer year;
    }
}
