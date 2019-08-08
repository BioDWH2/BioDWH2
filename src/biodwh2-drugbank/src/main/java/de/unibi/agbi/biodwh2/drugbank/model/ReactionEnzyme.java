package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ReactionEnzyme {
    @JsonProperty("drugbank-id")
    public String drugbankId;
    public String name;
    @JsonProperty("uniprot-id")
    public String uniprotId;
}
