package de.unibi.agbi.biodwh2.rnainter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphEdgeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.rnainter.etl.RNAInterGraphExporter;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "RNAInterID", "Interactor1.Symbol", "Category1", "Species1", "Interactor2.Symbol", "Category2", "Species2",
        "Raw_ID1", "Raw_ID2", "score", "strong", "weak", "predict"
})
@GraphEdgeLabel(RNAInterGraphExporter.INTERACTS_WITH_LABEL)
public class Entry {
    @JsonProperty("RNAInterID")
    public String rnaInterId;
    @JsonProperty("Interactor1.Symbol")
    public String interactor1Symbol;
    @JsonProperty("Category1")
    public String category1;
    @JsonProperty("Species1")
    public String species1;
    @JsonProperty("Interactor2.Symbol")
    public String interactor2Symbol;
    @JsonProperty("Category2")
    public String category2;
    @JsonProperty("Species2")
    public String species2;
    @JsonProperty("Raw_ID1")
    public String rawId1;
    @JsonProperty("Raw_ID2")
    public String rawId2;
    @JsonProperty("score")
    @GraphProperty(value = "score", emptyPlaceholder = RNAInterGraphExporter.NOT_AVAILABLE_VALUE)
    public String score;
    @JsonProperty("strong")
    @GraphArrayProperty(value = "strong", arrayDelimiter = "//", emptyPlaceholder = RNAInterGraphExporter.NOT_AVAILABLE_VALUE)
    public String strong;
    @JsonProperty("weak")
    @GraphArrayProperty(value = "weak", arrayDelimiter = "//", emptyPlaceholder = RNAInterGraphExporter.NOT_AVAILABLE_VALUE)
    public String weak;
    @JsonProperty("predict")
    @GraphArrayProperty(value = "predict", arrayDelimiter = "//", emptyPlaceholder = RNAInterGraphExporter.NOT_AVAILABLE_VALUE)
    public String predict;
}
