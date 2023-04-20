package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("ClinicalVariant")
@JsonPropertyOrder({
        "variant", "gene", "type", "level of evidence", "chemicals", "phenotypes"
})
public class ClinicalVariant {
    @JsonProperty("variant")
    public String variant;
    @JsonProperty("gene")
    public String gene;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("level of evidence")
    @GraphProperty("level_of_evidence")
    public String levelOfEvidence;
    @JsonProperty("chemicals")
    public String chemicals;
    @JsonProperty("phenotypes")
    public String phenotypes;
}
