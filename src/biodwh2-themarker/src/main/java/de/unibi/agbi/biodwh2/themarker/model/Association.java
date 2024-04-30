package de.unibi.agbi.biodwh2.themarker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.themarker.etl.TheMarkerGraphExporter;

@JsonPropertyOrder({
        "Biomarker ID", "Drug ID", "Disease ID", "Biomarker Discovered From", "Target engagement Drug Class",
        "Biomarker Mode", "Biomarker Level", "Experimental Species", "Biomarker Source", "Testing Methods",
        "Description", "Biomarker Class", "ADR", "ADR Type", "SE Status", "Patient age", "log2FC", "pvalue", "padj"
})
@GraphNodeLabel(TheMarkerGraphExporter.ASSOCIATION_LABEL)
public class Association {
    @JsonProperty("Biomarker ID")
    public String biomarkerId;
    @JsonProperty("Drug ID")
    public String drugId;
    @JsonProperty("Disease ID")
    public String diseaseId;
    @JsonProperty("Biomarker Discovered From")
    @GraphProperty(value = "biomarker_discovered_from", emptyPlaceholder = ".", ignoreEmpty = true)
    public String biomarkerDiscoveredFrom;
    @JsonProperty("Target engagement Drug Class")
    @GraphProperty(value = "target_engagement_drug_class", emptyPlaceholder = ".", ignoreEmpty = true)
    public String targetEngagementDrugClass;
    @JsonProperty("Biomarker Mode")
    @GraphProperty(value = "biomarker_mode", emptyPlaceholder = ".", ignoreEmpty = true)
    public String biomarkerMode;
    @JsonProperty("Biomarker Level")
    @GraphProperty(value = "biomarker_level", emptyPlaceholder = ".", ignoreEmpty = true)
    public String biomarkerLevel;
    @JsonProperty("Experimental Species")
    @GraphProperty(value = "experimental_species", emptyPlaceholder = ".", ignoreEmpty = true)
    public String experimentalSpecies;
    @JsonProperty("Biomarker Source")
    @GraphProperty(value = "biomarker_source", emptyPlaceholder = ".", ignoreEmpty = true)
    public String biomarkerSource;
    @JsonProperty("Testing Methods")
    @GraphProperty(value = "testing_methods", emptyPlaceholder = ".", ignoreEmpty = true)
    public String testingMethods;
    @JsonProperty("Description")
    @GraphProperty(value = "description", emptyPlaceholder = ".", ignoreEmpty = true)
    public String description;
    @JsonProperty("Biomarker Class")
    @GraphProperty(value = "biomarker_class", emptyPlaceholder = ".", ignoreEmpty = true)
    public String biomarkerClass;
    @JsonProperty("ADR")
    @GraphProperty(value = "adr", emptyPlaceholder = ".", ignoreEmpty = true)
    public String adr;
    @JsonProperty("ADR Type")
    @GraphProperty(value = "adr_type", emptyPlaceholder = ".", ignoreEmpty = true)
    public String adrType;
    @JsonProperty("SE Status")
    @GraphProperty(value = "se_status", emptyPlaceholder = ".", ignoreEmpty = true)
    public String seStatus;
    @JsonProperty("Patient age")
    @GraphProperty(value = "patient_age", emptyPlaceholder = ".", ignoreEmpty = true)
    public String patientAge;
    @JsonProperty("log2FC")
    @GraphProperty(value = "log2fc", ignoreEmpty = true)
    public String log2FC;
    @JsonProperty("pvalue")
    @GraphProperty(value = "pvalue", ignoreEmpty = true)
    public String pValue;
    @JsonProperty("padj")
    @GraphProperty(value = "padj", ignoreEmpty = true)
    public String padj;
}
