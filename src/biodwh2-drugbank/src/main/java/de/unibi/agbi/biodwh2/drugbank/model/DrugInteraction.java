package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class DrugInteraction {
    @JsonProperty("drugbank-id")
    public DrugbankDrugSaltId drugbankId;
    public String name;
    public String description;
}
