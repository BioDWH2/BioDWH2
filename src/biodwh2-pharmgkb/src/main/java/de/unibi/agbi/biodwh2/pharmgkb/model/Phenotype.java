package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("Phenotype")
@JsonPropertyOrder({
        "PharmGKB Accession Id", "Name", "Alternate Names", "Cross-references", "External Vocabulary"
})
public class Phenotype {
    @JsonProperty("PharmGKB Accession Id")
    @GraphProperty("id")
    public String pharmgkbAccessionId;
    @JsonProperty("Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Alternate Names")
    @GraphArrayProperty(value = "alternate_names", arrayDelimiter = ", ", quotedArrayElements = true)
    public String alternateNames;
    @JsonProperty("Cross-references")
    @GraphArrayProperty(value = "cross_references", arrayDelimiter = ", ", quotedArrayElements = true)
    public String crossReferences;
    @JsonProperty("External Vocabulary")
    public String externalVocabulary;
}
