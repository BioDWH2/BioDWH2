package de.unibi.agbi.biodwh2.herb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "EXP_id", "Herb/ingredient_id", "Herb/ingredient_name", "Herb/ingredient", "GSE_id", "Organism", "Strain",
        "Tissue", "Cell_Type", "Cell_line", "Experiment_type", "Sequence_type", "Experiment_subject",
        "Experiment_subject_detail", "Experiment_special_pretreatment", "Control_condition", "Control_samples",
        "Treatment_condition", "Treatment_samples", "Drug_delivery", "Plat_info"
})
@GraphNodeLabel("Experiment")
public class Experiment {
    @JsonProperty("EXP_id")
    @GraphProperty("id")
    public String expId;
    @JsonProperty("Herb/ingredient_id")
    public String herbOrIngredientId;
    @JsonProperty("Herb/ingredient_name")
    public String herbOrIngredientName;
    @JsonProperty("Herb/ingredient")
    public String herbOrIngredient;
    @JsonProperty("GSE_id")
    @GraphProperty("gse_id")
    public String gseId;
    @JsonProperty("Organism")
    @GraphProperty("organism")
    public String organism;
    @JsonProperty("Strain")
    @GraphProperty("strain")
    public String strain;
    @JsonProperty("Tissue")
    @GraphProperty("tissue")
    public String tissue;
    @JsonProperty("Cell_Type")
    @GraphProperty("cell_type")
    public String cellType;
    @JsonProperty("Cell_line")
    @GraphProperty("cell_line")
    public String cellLine;
    @JsonProperty("Experiment_type")
    @GraphProperty("type")
    public String experimentType;
    @JsonProperty("Sequence_type")
    @GraphProperty("sequence_type")
    public String sequenceType;
    @JsonProperty("Experiment_subject")
    @GraphProperty("subject")
    public String experimentSubject;
    @JsonProperty("Experiment_subject_detail")
    @GraphProperty("subject_detail")
    public String experimentSubjectDetail;
    @JsonProperty("Experiment_special_pretreatment")
    @GraphProperty(value = "special_pretreatment", emptyPlaceholder = "/")
    public String experimentSpecialPretreatment;
    @JsonProperty("Control_condition")
    @GraphProperty("control_condition")
    public String controlCondition;
    @JsonProperty("Control_samples")
    @GraphArrayProperty(value = "control_samples", arrayDelimiter = "; ")
    public String controlSamples;
    @JsonProperty("Treatment_condition")
    @GraphProperty("treatment_condition")
    public String treatmentCondition;
    @JsonProperty("Treatment_samples")
    @GraphArrayProperty(value = "treatment_samples", arrayDelimiter = "; ")
    public String treatmentSamples;
    @JsonProperty("Drug_delivery")
    @GraphProperty("drug_delivery")
    public String drugDelivery;
    @JsonProperty("Plat_info")
    @GraphProperty("platform")
    public String platInfo;
}
