package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reference {
    @JsonProperty("reference_text")
    public String referenceText;
    @JsonProperty("pubmed_id")
    public Integer pubmedId;
}
