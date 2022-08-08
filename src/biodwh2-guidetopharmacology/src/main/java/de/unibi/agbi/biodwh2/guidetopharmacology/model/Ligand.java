package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "ligand_id", "name", "pubchem_sid", "radioactive", "old_ligand_id", "type", "approved", "approved_source",
        "iupac_name", "comments", "withdrawn_drug", "verified", "abbreviation", "clinical_use", "mechanism_of_action",
        "absorption_distribution", "metabolism", "elimination", "popn_pharmacokinetics", "organ_function_impairment",
        "emc_url", "drugs_url", "ema_url", "bioactivity_comments", "labelled", "in_gtip", "immuno_comments", "in_gtmp",
        "gtmp_comments", "who_essential", "antibiotic", "has_interaction", "name_vector", "comments_vector",
        "abbreviation_vector", "clinical_use_vector", "mechanism_of_action_vector", "absorption_distribution_vector",
        "metabolism_vector", "elimination_vector", "popn_pharmacokinetics_vector", "organ_function_impairment_vector",
        "bioactivity_comments_vector", "immuno_comments_vector", "gtmp_comments_vector"
})
@GraphNodeLabel("Ligand")
public class Ligand {
    @JsonProperty("ligand_id")
    @GraphProperty("id")
    public Long ligandId;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("pubchem_sid")
    @GraphProperty("pubchem_sid")
    public Long pubchemSid;
    @JsonProperty("radioactive")
    @GraphBooleanProperty(value = "radioactive", truthValue = "t")
    public String radioactive;
    @JsonProperty("old_ligand_id")
    @GraphProperty("old_id")
    public Long oldLigandId;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("approved")
    @GraphBooleanProperty(value = "approved", truthValue = "t")
    public String approved;
    @JsonProperty("approved_source")
    @GraphProperty("approved_source")
    public String approvedSource;
    @JsonProperty("iupac_name")
    @GraphProperty("iupac_name")
    public String iupacName;
    @JsonProperty("comments")
    @GraphProperty("comments")
    public String comments;
    @JsonProperty("withdrawn_drug")
    @GraphBooleanProperty(value = "withdrawn_drug", truthValue = "t")
    public String withdrawnDrug;
    @JsonProperty("verified")
    @GraphBooleanProperty(value = "verified", truthValue = "t")
    public String verified;
    @JsonProperty("abbreviation")
    @GraphProperty("abbreviation")
    public String abbreviation;
    @JsonProperty("clinical_use")
    @GraphProperty("clinical_use")
    public String clinicalUse;
    @JsonProperty("mechanism_of_action")
    @GraphProperty("mechanism_of_action")
    public String mechanismOfAction;
    @JsonProperty("absorption_distribution")
    @GraphProperty("absorption_distribution")
    public String absorptionDistribution;
    @JsonProperty("metabolism")
    @GraphProperty("metabolism")
    public String metabolism;
    @JsonProperty("elimination")
    @GraphProperty("elimination")
    public String elimination;
    @JsonProperty("popn_pharmacokinetics")
    @GraphProperty("popn_pharmacokinetics")
    public String popnPharmacokinetics;
    @JsonProperty("organ_function_impairment")
    @GraphProperty("organ_function_impairment")
    public String organFunctionImpairment;
    @JsonProperty("emc_url")
    @GraphProperty("emc_url")
    public String emcUrl;
    @JsonProperty("drugs_url")
    @GraphProperty("drugs_url")
    public String drugsUrl;
    @JsonProperty("ema_url")
    @GraphProperty("ema_url")
    public String emaUrl;
    @JsonProperty("bioactivity_comments")
    @GraphProperty("bioactivity_comments")
    public String bioactivityComments;
    @JsonProperty("labelled")
    @GraphBooleanProperty(value = "labelled", truthValue = "t")
    public String labelled;
    @JsonProperty("in_gtip")
    @GraphBooleanProperty(value = "in_gtip", truthValue = "t")
    public String inGtip;
    @JsonProperty("immuno_comments")
    @GraphProperty("immuno_comments")
    public String immunoComments;
    @JsonProperty("in_gtmp")
    @GraphBooleanProperty(value = "in_gtmp", truthValue = "t")
    public String inGtmp;
    @JsonProperty("gtmp_comments")
    @GraphProperty("gtmp_comments")
    public String gtmpComments;
    @JsonProperty("who_essential")
    @GraphBooleanProperty(value = "who_essential", truthValue = "t")
    public String whoEssential;
    @JsonProperty("antibiotic")
    @GraphBooleanProperty(value = "antibiotic", truthValue = "t")
    public String antibiotic;
    @JsonProperty("has_interaction")
    @GraphBooleanProperty(value = "has_interaction", truthValue = "t")
    public String hasInteraction;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("name_vector")
    public String nameVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("comments_vector")
    public String commentsVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("abbreviation_vector")
    public String abbreviationVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("clinical_use_vector")
    public String clinicalUseVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("mechanism_of_action_vector")
    public String mechanismOfActionVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("absorption_distribution_vector")
    public String absorptionDistributionVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("metabolism_vector")
    public String metabolismVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("elimination_vector")
    public String eliminationVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("popn_pharmacokinetics_vector")
    public String popnPharmacokineticsVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("organ_function_impairment_vector")
    public String organFunctionImpairmentVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("bioactivity_comments_vector")
    public String bioactivityCommentsVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("immuno_comments_vector")
    public String immunoCommentsVector;
    /**
     * Full text search vectors for Postgresql. Can be ignored.
     */
    @JsonProperty("gtmp_comments_vector")
    public String gtmpCommentsVector;
}
