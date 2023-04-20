package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@SuppressWarnings("unused")
@GraphNodeLabel("ClinicalAnnotationAllele")
@JsonPropertyOrder({
        "Clinical Annotation ID", "Genotype/Allele", "Annotation Text", "Allele Function"
})
public class ClinicalAnnotationAllele {
    @JsonProperty("Clinical Annotation ID")
    public Integer clinicalAnnotationId;
    @JsonProperty("Genotype/Allele")
    @GraphProperty("genotype_or_allele")
    public String genotypeAllele;
    @JsonProperty("Annotation Text")
    @GraphProperty("text")
    public String annotationText;
    @JsonProperty("Allele Function")
    @GraphProperty("allele_function")
    public String alleleFunction;
}
