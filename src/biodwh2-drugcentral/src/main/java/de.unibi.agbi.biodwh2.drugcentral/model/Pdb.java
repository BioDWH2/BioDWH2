package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId", "pdb", "chainId", "accession", "title", "pubmedId",
                            "expMethod", "depositionDate", "ligandId"})
public final class Pdb {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("pdb")
    public String pdb;
    @JsonProperty("chainId")
    public String chainId;
    @JsonProperty("accession")
    public String accession;
    @JsonProperty("title")
    public String title;
    @JsonProperty("pubmedId")
    public String pubmedId;
    @JsonProperty("expMethod")
    public String expMethod;
    @JsonProperty("depositionDate")
    public String depositionDate;
    @JsonProperty("ligandId")
    public String ligandId;
}
