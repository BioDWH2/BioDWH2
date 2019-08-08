package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class PathwayDrug {
    @JsonProperty("drugbank-id")
    public DrugbankDrugSaltId drugbankId;
    public String name;
}
