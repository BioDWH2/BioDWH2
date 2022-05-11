package de.unibi.agbi.biodwh2.herb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "REF_id", "Herb/ingredient_id", "Herb/ingredient_name", "Herb/ingredient", "Reference_title", "PubMed_id",
        "Journal", "Publish.Date", "DOI", "Experiment_subject", "Experiment_type", "Animal_Experiment",
        "Cell_Experiment", "Clinical_Experiment", "Not_Mentioned"
})
public class Reference {
    @JsonProperty("REF_id")
    public String refId;
    @JsonProperty("Herb/ingredient_id")
    public String herbOrIngredientId;
    @JsonProperty("Herb/ingredient_name")
    public String herbOrIngredientName;
    @JsonProperty("Herb/ingredient")
    public String herbOrIngredient;
    @JsonProperty("Reference_title")
    public String referenceTitle;
    @JsonProperty("PubMed_id")
    public String pubMedId;
    @JsonProperty("Journal")
    public String journal;
    @JsonProperty("Publish.Date")
    public String publishDate;
    @JsonProperty("DOI")
    public String doi;
    @JsonProperty("Experiment_subject")
    public String experimentSubject;
    @JsonProperty("Experiment_type")
    public String experimentType;
    @JsonProperty("Animal_Experiment")
    public String animalExperiment;
    @JsonProperty("Cell_Experiment")
    public String cellExperiment;
    @JsonProperty("Clinical_Experiment")
    public String clinicalExperiment;
    @JsonProperty("Not_Mentioned")
    public String notMentioned;
}
