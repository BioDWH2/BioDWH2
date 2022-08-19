package de.unibi.agbi.biodwh2.adrecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({"ADRECS_ID", "ADR_ID", "ADR_TERM", "ADR_SYNONYMS", "MEDDRA_CODE"})
@GraphNodeLabel("ADR")
public class ADROntologyEntry {
    @JsonProperty("ADRECS_ID")
    @GraphProperty("adrecs_id")
    public String adrecsId;
    @JsonProperty("ADR_ID")
    @GraphProperty("id")
    public String adrId;
    @JsonProperty("ADR_TERM")
    @GraphProperty("term")
    public String adrTerm;
    @JsonProperty("ADR_SYNONYMS")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = " | ", emptyPlaceholder = "Not Available")
    public String adrSynonyms;
    @JsonProperty("MEDDRA_CODE")
    @GraphProperty("meddra_code")
    public String meddraCode;
}
