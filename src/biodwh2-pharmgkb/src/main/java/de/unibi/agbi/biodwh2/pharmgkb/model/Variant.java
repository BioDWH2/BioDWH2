package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@SuppressWarnings("unused")
@GraphNodeLabel("Variant")
@JsonPropertyOrder({
        "Variant ID", "Variant Name", "Gene IDs", "Gene Symbols", "Location", "Variant Annotation count",
        "Clinical Annotation count", "Level 1/2 Clinical Annotation count", "Guideline Annotation count",
        "Label Annotation count", "Synonyms"
})
public class Variant {
    @JsonProperty("Variant ID")
    @GraphProperty("id")
    public String variantId;
    @JsonProperty("Variant Name")
    @GraphProperty("name")
    public String variantName;
    @JsonProperty("Gene IDs")
    @GraphArrayProperty(value = "gene_ids", arrayDelimiter = ",")
    public String geneIds;
    @JsonProperty("Gene Symbols")
    @GraphArrayProperty(value = "gene_symbols", arrayDelimiter = ",")
    public String geneSymbols;
    @JsonProperty("Location")
    @GraphProperty("location")
    public String location;
    @JsonProperty("Variant Annotation count")
    @GraphProperty("variant_annotation_count")
    public Integer variantAnnotationCount;
    @JsonProperty("Clinical Annotation count")
    @GraphProperty("clinical_annotation_count")
    public Integer clinicalAnnotationCount;
    @JsonProperty("Level 1/2 Clinical Annotation count")
    @GraphProperty("level12_clinical_annotation_count")
    public Integer level12ClinicalAnnotationCount;
    @JsonProperty("Guideline Annotation count")
    @GraphProperty("guideline_annotation_count")
    public Integer guidelineAnnotationCount;
    @JsonProperty("Label Annotation count")
    @GraphProperty("label_annotation_count")
    public Integer labelAnnotationCount;
    @JsonProperty("Synonyms")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = ", ")
    public String synonyms;
}
