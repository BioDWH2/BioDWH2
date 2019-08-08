package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ReactionElement {
    @JsonProperty("drugbank-id")
    public String drugbankId;
    public String name;
}
