package de.unibi.agbi.biodwh2.themarker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Biomarker ID", "Drug ID", "Disease ID", "Biomarker Discovered From", "Target engagement Drug Class",
        "Biomarker Mode", "Biomarker Level", "Experimental Species", "Biomarker Source", "Testing Methods",
        "Description", "Biomarker Class", "ADR", "ADR Type", "SE Status", "Patient age", "log2FC", "pvalue", "padj"
})
public class Association {
    @JsonProperty("Biomarker ID")
    public String biomarkerId;
    @JsonProperty("Drug ID")
    public String drugId;
    @JsonProperty("Disease ID")
    public String diseaseId;
    @JsonProperty("Biomarker Discovered From")
    public String biomarkerDiscoveredFrom;
    @JsonProperty("Target engagement Drug Class")
    public String targetEngagementDrugClass;
    @JsonProperty("Biomarker Mode")
    public String biomarkerMode;
    @JsonProperty("Biomarker Level")
    public String biomarkerLevel;
    @JsonProperty("Experimental Species")
    public String experimentalSpecies;
    @JsonProperty("Biomarker Source")
    public String biomarkerSource;
    @JsonProperty("Testing Methods")
    public String testingMethods;
    @JsonProperty("Description")
    public String description;
    @JsonProperty("Biomarker Class")
    public String biomarkerClass;
    @JsonProperty("ADR")
    public String adr;
    @JsonProperty("ADR Type")
    public String adrType;
    @JsonProperty("SE Status")
    public String seStatus;
    @JsonProperty("Patient age")
    public String patientAge;
    @JsonProperty("log2FC")
    public String log2FC;
    @JsonProperty("pvalue")
    public String pValue;
    @JsonProperty("padj")
    public String padj;
}
