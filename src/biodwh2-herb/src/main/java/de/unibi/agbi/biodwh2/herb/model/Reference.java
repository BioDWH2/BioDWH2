package de.unibi.agbi.biodwh2.herb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.herb.etl.HerbGraphExporter;

@JsonPropertyOrder({
        "REF_id", "_unknown", "Herb/ingredient_id", "Herb/ingredient_name", "Herb/ingredient", "Reference_title",
        "Reference_abstract", "PubMed_id", "Journal", "Publish.Date", "DOI", "Experiment_subject", "Experiment_type",
        "Experiment_disease", "Animal_Experiment", "Cell_Experiment", "Clinical_Experiment", "Not_Mentioned"
})
@GraphNodeLabel(HerbGraphExporter.REFERENCE_LABEL)
public class Reference {
    @JsonProperty("REF_id")
    @GraphProperty("id")
    public String refId;
    @JsonProperty("_unknown")
    private String unknown;
    @JsonProperty("Herb/ingredient_id")
    public String herbOrIngredientId;
    @JsonProperty("Herb/ingredient_name")
    public String herbOrIngredientName;
    @JsonProperty("Herb/ingredient")
    public String herbOrIngredient;
    @JsonProperty("Reference_title")
    @GraphProperty(value = "title", emptyPlaceholder = "NA")
    public String referenceTitle;
    @JsonProperty("Reference_abstract")
    @GraphProperty(value = "abstract", emptyPlaceholder = "NA")
    public String referenceAbstract;
    @JsonProperty("PubMed_id")
    @GraphNumberProperty(value = "pubmed_id", emptyPlaceholder = "NA")
    public String pubMedId;
    @JsonProperty("Journal")
    @GraphProperty(value = "journal", emptyPlaceholder = "NA")
    public String journal;
    @JsonProperty("Publish.Date")
    @GraphProperty(value = "publish_date", emptyPlaceholder = "NA")
    public String publishDate;
    @JsonProperty("DOI")
    @GraphProperty(value = "doi", emptyPlaceholder = "NA")
    public String doi;
    @JsonProperty("Experiment_subject")
    @GraphProperty(value = "experiment_subject", emptyPlaceholder = "NA")
    public String experimentSubject;
    @JsonProperty("Experiment_type")
    @GraphProperty(value = "experiment_type", emptyPlaceholder = "NA")
    public String experimentType;
    @JsonProperty("Experiment_disease")
    @GraphProperty(value = "experiment_disease", emptyPlaceholder = "NA")
    public String experimentDisease;
    @JsonProperty("Animal_Experiment")
    @GraphBooleanProperty(value = "animal_experiment", truthValue = "1")
    public String animalExperiment;
    @JsonProperty("Cell_Experiment")
    @GraphBooleanProperty(value = "cell_experiment", truthValue = "1")
    public String cellExperiment;
    @JsonProperty("Clinical_Experiment")
    @GraphBooleanProperty(value = "clinical_experiment", truthValue = "1")
    public String clinicalExperiment;
    @JsonProperty("Not_Mentioned")
    @GraphBooleanProperty(value = "not_mentioned", truthValue = "1")
    public String notMentioned;
}
