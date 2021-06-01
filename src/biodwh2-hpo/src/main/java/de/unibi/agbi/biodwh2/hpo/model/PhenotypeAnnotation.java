package de.unibi.agbi.biodwh2.hpo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "DatabaseID", "DiseaseName", "Qualifier", "HPO_ID", "Reference", "Evidence", "Onset", "Frequency", "Sex",
        "Modifier", "Aspect", "Biocuration"
})
public final class PhenotypeAnnotation {
    @JsonProperty("DatabaseID")
    public String databaseId;
    @JsonProperty("DiseaseName")
    public String diseaseName;
    @JsonProperty("Qualifier")
    public String qualifier;
    @JsonProperty("HPO_ID")
    public String hpoId;
    @JsonProperty("Reference")
    public String reference;
    @JsonProperty("Evidence")
    public EvidenceCode evidence;
    @JsonProperty("Onset")
    public String onset;
    @JsonProperty("Frequency")
    public String frequency;
    @JsonProperty("Sex")
    public String sex;
    @JsonProperty("Modifier")
    public String modifier;
    @JsonProperty("Aspect")
    public String aspect;
    @JsonProperty("Biocuration")
    public String biocuration;
}
