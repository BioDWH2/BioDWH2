package de.unibi.agbi.biodwh2.hpo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "database_id", "disease_name", "qualifier", "hpo_id", "reference", "evidence", "onset", "frequency", "sex",
        "modifier", "aspect", "biocuration"
})
public final class PhenotypeAnnotation {
    @JsonProperty("database_id")
    public String databaseId;
    @JsonProperty("disease_name")
    public String diseaseName;
    @JsonProperty("qualifier")
    public String qualifier;
    @JsonProperty("hpo_id")
    public String hpoId;
    @JsonProperty("reference")
    public String reference;
    @JsonProperty("evidence")
    public EvidenceCode evidence;
    @JsonProperty("onset")
    public String onset;
    @JsonProperty("frequency")
    public String frequency;
    @JsonProperty("sex")
    public String sex;
    @JsonProperty("modifier")
    public String modifier;
    @JsonProperty("aspect")
    public String aspect;
    @JsonProperty("biocuration")
    public String biocuration;
}
